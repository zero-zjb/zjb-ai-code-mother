package com.zjb.zjbaicodemother.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zjb.zjbaicodemother.constant.UserConstant;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.exception.ThrowUtils;
import com.zjb.zjbaicodemother.model.dto.ChatHistoryQueryRequest;
import com.zjb.zjbaicodemother.model.entity.App;
import com.zjb.zjbaicodemother.model.entity.ChatHistory;
import com.zjb.zjbaicodemother.mapper.ChatHistoryMapper;
import com.zjb.zjbaicodemother.model.entity.User;
import com.zjb.zjbaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.zjb.zjbaicodemother.service.AppService;
import com.zjb.zjbaicodemother.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author zjb
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

    @Resource
    @Lazy
    private AppService appService;
    /**
     * 添加对话历史
     *
     * @param appId       应用id
     * @param message     消息
     * @param messageType 消息类型
     * @param userId      用户id
     * @return 是否添加成功
     */
    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
        //1.校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id错误");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户id错误");
        //2.校验消息类型是否有效
        ChatHistoryMessageTypeEnum enumByValue = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(enumByValue == null, ErrorCode.PARAMS_ERROR, "不支持该消息类型");
        //3.保存历史对话
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();
        return save(chatHistory);
    }

    /**
     * 删除指定应用的所有对话历史
     *
     * @param appId 应用id
     * @return 是否删除成功
     */
    @Override
    public boolean deleteByAppId(Long appId) {
        //1.校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id错误");
        return remove(QueryWrapper.create().eq("appId", appId));
    }

    /**
     * 获取指定应用的对话历史列表
     *
     * @param appId           应用id
     * @param pageSize        每页大小
     * @param lastCreateTime  最后创建时间
     * @param loginUser       登录用户
     * @return 对话历史列表
     */
    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser) {
        //1.校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id错误");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "每页大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        //2.校验应用是否存在
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //3.校验用户权限
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE), ErrorCode.NO_AUTH_ERROR, "无访问权限");
        //4.构造查询条件
        ChatHistoryQueryRequest chatHistoryQueryRequest = new ChatHistoryQueryRequest();
        chatHistoryQueryRequest.setAppId(appId);
        chatHistoryQueryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(chatHistoryQueryRequest);
        //5.分页查询
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    /**
     * 加载指定应用的对话历史到消息窗口
     *
     * @param appId       应用id
     * @param chatMessage 消息窗口
     * @param maxCount    最大数量
     * @return 加载数量
     */
    @Override
    public int loadChatHistoryToMessage(Long appId, MessageWindowChatMemory chatMessage, int maxCount) {
        try {
            //直接构造查询条件，起始点为1而不是0，用于排除最新的用户消息
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime, false)
                    .limit(1, maxCount);
            List<ChatHistory> list = list(queryWrapper);
            if(CollUtil.isEmpty(list)){
                return 0;
            }
            //反转列表，确保最新的消息在最后面
            list = CollUtil.reverse(list);
            //按时间顺序添加到消息窗口
            int loadedCount = 0;
            //先清理历史缓存，防止缓存数据重复
            chatMessage.clear();
            for (ChatHistory chatHistory : list) {
                if(chatHistory.getMessageType().equals(ChatHistoryMessageTypeEnum.USER.getValue())){
                    chatMessage.add(UserMessage.from(chatHistory.getMessage()));
                    loadedCount++;
                } else if(chatHistory.getMessageType().equals(ChatHistoryMessageTypeEnum.AI.getValue())){
                    chatMessage.add(AiMessage.from(chatHistory.getMessage()));
                    loadedCount++;
                }
            }
            return loadedCount;
        } catch (Exception e) {
            log.error("加载对话历史失败, appId: {}, error: {}", appId, e.getMessage(), e);
            //加载失败不影响系统运行，只是没有历史上下文
            return 0;
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest
     * @return 查询包装类
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq("id", id)
                .like("message", message)
                .eq("messageType", messageType)
                .eq("appId", appId)
                .eq("userId", userId);
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("createTime", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderBy("createTime", false);
        }
        return queryWrapper;
    }
}

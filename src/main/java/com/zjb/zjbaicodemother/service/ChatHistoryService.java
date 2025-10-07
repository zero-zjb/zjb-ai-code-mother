package com.zjb.zjbaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.zjb.zjbaicodemother.model.dto.ChatHistoryQueryRequest;
import com.zjb.zjbaicodemother.model.entity.ChatHistory;
import com.zjb.zjbaicodemother.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author zjb
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     *
     * @param appId      应用 ID
     * @param message    消息
     * @param messageType 消息类型
     * @param userId     用户 ID
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 删除对话历史
     * @param appId 应用 ID
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest 对话历史查询条件
     * @return 查询包装类
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 获取指定应用的对话历史分页列表
     *
     * @param appId 应用 ID
     * @param pageSize 每页大小
     * @param lastCreateTime 最后创建时间
     * @param loginUser 登录用户
     * @return 对话历史分页列表
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    /**
     * 加载指定应用的对话历史
     *
     * @param appId 应用 ID
     * @param chatMessage 对话记忆
     * @param maxCount 最对加载maxCount条对话历史
     * @return loadedCount 加载的条数
     */
    int loadChatHistoryToMessage(Long appId, MessageWindowChatMemory chatMessage, int maxCount);
}

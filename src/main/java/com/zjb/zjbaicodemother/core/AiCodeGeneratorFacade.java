package com.zjb.zjbaicodemother.core;

import cn.hutool.json.JSONUtil;
import com.zjb.zjbaicodemother.ai.AiCodeGeneratorService;
import com.zjb.zjbaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.zjb.zjbaicodemother.ai.model.HtmlCodeResult;
import com.zjb.zjbaicodemother.ai.model.MultiFileCodeResult;
import com.zjb.zjbaicodemother.ai.model.message.AiResponseMessage;
import com.zjb.zjbaicodemother.ai.model.message.ToolExecutedMessage;
import com.zjb.zjbaicodemother.ai.model.message.ToolRequestMessage;
import com.zjb.zjbaicodemother.core.parser.CodeParserExecutor;
import com.zjb.zjbaicodemother.core.saver.CodeFileSaverExecutor;
import com.zjb.zjbaicodemother.exception.BusinessException;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage, appId);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage, appId);
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId 应用ID
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage, appId);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage, appId);
            case VUE_PROJECT -> generateAndSaveVueProjectCodeStream(userMessage, appId);
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 通用流式代码处理方法
     *
     * @param codeStream      代码流
     * @param codeGenType 生成类型
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId){
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(
                chunk -> {
                    //实时收集代码片段
                    codeBuilder.append(chunk);
                }
        ).doOnComplete(() -> {
            try {
                String completeCode = codeBuilder.toString();
                //使用解析器解析代码
                Object parsedCode = CodeParserExecutor.executeParser(completeCode, codeGenType);
                //使用执行器保存代码
                File saveDir = CodeFileSaverExecutor.executeSaver(parsedCode, codeGenType, appId);
                log.info("代码保存成功，保存目录：{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("代码保存失败: {}", e.getMessage());
            }
        });
    }

    /**
     * 生成HTML模式的代码并保存（流式）
     *
     * @param userMessage     用户提示词
     * @return 响应流
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage, Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        //当流式返回生成代码完成后，在保存代码
        return processCodeStream(result, CodeGenTypeEnum.HTML, appId);
    }

    /**
     * 生成多文件模式的代码并保存（流式）
     *
     * @param userMessage     用户提示词
     * @param appId 应用ID
     * @return 响应流
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage, Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        //当流式返回生成代码完成后，在保存代码
        return processCodeStream(result, CodeGenTypeEnum.MULTI_FILE, appId);
    }

    /**
     * 创建Vue项目模式的代码并保存（流式）
     *
     * @param userMessage     用户提示词
     * @param appId 应用ID
     * @return 响应流
     */
    private Flux<String> generateAndSaveVueProjectCodeStream(String userMessage, Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, CodeGenTypeEnum.VUE_PROJECT);
        TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
        return processTokenStream(tokenStream);
    }

    /**
     * 将TokenStream转换为Flux<String>，并传递工具调用信息
     *
     * @param tokenStream      Token流
     * @return 响应流
     */
    private Flux<String> processTokenStream(TokenStream tokenStream){
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                sink.next(JSONUtil.toJsonStr(aiResponseMessage));
            }).onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                sink.next(JSONUtil.toJsonStr(toolRequestMessage));
            }).onToolExecuted((ToolExecution toolExecution) -> {
                ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
            }).onCompleteResponse((ChatResponse chatResponse) -> {
                sink.complete();
            }).onError((Throwable error) -> {
                error.printStackTrace();
                sink.error(error);
            }).start();
        });
    }

    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @param appId 应用ID
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage, Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @param appId 应用ID
     * @return 保存的目录
     */
    private File generateAndSaveMultiFileCode(String userMessage, Long appId) {
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE, appId);
    }
}

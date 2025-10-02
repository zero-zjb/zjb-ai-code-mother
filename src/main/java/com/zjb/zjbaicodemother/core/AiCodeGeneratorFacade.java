package com.zjb.zjbaicodemother.core;

import com.zjb.zjbaicodemother.ai.AiCodeGeneratorService;
import com.zjb.zjbaicodemother.ai.model.HtmlCodeResult;
import com.zjb.zjbaicodemother.ai.model.MultiFileCodeResult;
import com.zjb.zjbaicodemother.core.parser.CodeParserExecutor;
import com.zjb.zjbaicodemother.core.saver.CodeFileSaverExecutor;
import com.zjb.zjbaicodemother.exception.BusinessException;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.model.enums.CodeGenTypeEnum;
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
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
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
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
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
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType){
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
                File saveDir = CodeFileSaverExecutor.executeSaver(parsedCode, codeGenType);
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
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        //当流式返回生成代码完成后，在保存代码
        return processCodeStream(result, CodeGenTypeEnum.HTML);
    }

    /**
     * 生成多文件模式的代码并保存（流式）
     *
     * @param userMessage     用户提示词
     * @return 响应流
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        //当流式返回生成代码完成后，在保存代码
        return processCodeStream(result, CodeGenTypeEnum.MULTI_FILE);
    }

    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE);
    }
}

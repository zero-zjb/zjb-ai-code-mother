package com.zjb.zjbaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
/**
 * @Description: 多文件代码结果
 * @Author: zjb
 */
@Data
@Description("生成多文件代码的结果")
public class MultiFileCodeResult {

    /**
     * html代码
     */
    @Description("html代码")
    private String htmlCode;

    /**
     * css代码
     */
    @Description("css代码")
    private String cssCode;

    /**
     * js代码
     */
    @Description("js代码")
    private String jsCode;

    /**
     * 描述
     */
    @Description("生成代码的描述")
    private String description;
}

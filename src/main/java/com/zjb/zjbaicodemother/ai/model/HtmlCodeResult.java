package com.zjb.zjbaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
/**
 * @Description: html代码结果
 * @Author: zjb
 */
@Data
@Description("生成html代码文件的结果")
public class HtmlCodeResult {

    /**
     * html代码
     */
    @Description("html代码")
    private String htmlCode;

    /**
     * 描述
     */
    @Description("生成代码的描述")
    private String description;
}

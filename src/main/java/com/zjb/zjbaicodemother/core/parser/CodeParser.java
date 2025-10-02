package com.zjb.zjbaicodemother.core.parser;
/**
 * 描述：优化代码解析器，使用策略模式
 *
 * @author zjb
 */
public interface CodeParser<T> {
    /**
     * 解析代码
     *
     * @param codeContent 代码内容
     * @return 解析结果
     */
    T parse(String codeContent);
}

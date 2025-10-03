package com.zjb.zjbaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 应用添加请求
 * @author zjb
 */
@Data
public class AppAddRequest implements Serializable {

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    private static final long serialVersionUID = 1L;
}

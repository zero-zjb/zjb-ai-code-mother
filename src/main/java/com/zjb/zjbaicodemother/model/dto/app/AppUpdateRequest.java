package com.zjb.zjbaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 应用更新请求
 * @Author: zjb
 */
@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    private static final long serialVersionUID = 1L;
}

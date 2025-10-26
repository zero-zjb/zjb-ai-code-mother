package com.zjb.zjbaicodemother.service;

public interface ScreenshotService {

    /**
     * 生成网页截图并上传到对象存储
     *
     * @param webUrl 网页地址
     * @return 截图的访问URL，失败返回null
     */
    String generateAndUploadScreenshot(String webUrl);
}

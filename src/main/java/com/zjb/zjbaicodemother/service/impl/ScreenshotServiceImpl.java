package com.zjb.zjbaicodemother.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.exception.ThrowUtils;
import com.zjb.zjbaicodemother.manager.CosManager;
import com.zjb.zjbaicodemother.service.ScreenshotService;
import com.zjb.zjbaicodemother.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {
    @Resource
    private CosManager cosManager;

    /**
     * 生成网页截图并上传到对象存储
     *
     * @param webUrl 网页地址
     * @return 截图的访问URL，失败返回null
     */
    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页地址不能为空");
        log.info("开始生成网页截图，URL：{}", webUrl);
        //1. 生成本地截图
        String localScreenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(StrUtil.isBlank(localScreenshotPath), ErrorCode.OPERATION_ERROR, "本地截图生成失败");
        try {
            //2. 上传到对象存储
            String cosUrl = uploadScreenshotToCos(localScreenshotPath);
            ThrowUtils.throwIf(StrUtil.isBlank(cosUrl), ErrorCode.OPERATION_ERROR, "上传截图到对象存储失败");
            log.info("网页截图生成并上传成功:{}->{}", webUrl, cosUrl);
            return cosUrl;
        } finally {
            //3. 清理本地文件
            clearLocalFile(localScreenshotPath);
        }
    }

    /**
     * 上传截图到对象存储
     *
     * @param localScreenshotPath 本地截图路径
     * @return 对象存储访问URL，失败返回null
     */
    private String uploadScreenshotToCos(String localScreenshotPath) {
        if(StrUtil.isBlank(localScreenshotPath)){
            return null;
        }
        File screenshotFile = new File(localScreenshotPath);
        if(!screenshotFile.exists()){
            log.error("截图文件不存在：{}", localScreenshotPath);
            return null;
        }
        // 生成对象存储键
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        String cosKey = generateScreenshotKey(fileName);
        String url = cosManager.uploadFile(cosKey, screenshotFile);
        return url;
    }

    /**
     * 生成截图的对象存储键
     * 格式：/screenshots/2025/07/31/filename.jpg
     */
    private String generateScreenshotKey(String filename) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("/screenshots/%s/%s", datePath, filename);
    }

    /**
     * 清理本地文件
     *
     * @param localFilePath 本地文件路径
     */
    private void clearLocalFile(String localFilePath) {
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            File parentDir = localFile.getParentFile();
            FileUtil.del(parentDir);
            log.info("清理本地文件成功：{}", localFilePath);
        }
    }
}

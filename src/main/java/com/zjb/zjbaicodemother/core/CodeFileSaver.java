package com.zjb.zjbaicodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.zjb.zjbaicodemother.ai.model.HtmlCodeResult;
import com.zjb.zjbaicodemother.ai.model.MultiFileCodeResult;
import com.zjb.zjbaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Description: 代码文件保存器
 * @Author: zjb
 */
@Deprecated
public class CodeFileSaver {

    //文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "tmp/code_output";

    /**
     * 保存单文件代码结果
     *
     * @param htmlCodeResult  单文件代码结果
     * @return 保存后的目录
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult){
        String baseDirPath = buildUniquePath(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存多文件代码结果
     *
     * @param multiFileCodeResult  多文件代码结果
     * @return 保存后的目录
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult){
        String baseDirPath = buildUniquePath(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath, "index.html", multiFileCodeResult.getHtmlCode());
        writeToFile(baseDirPath, "index.css", multiFileCodeResult.getCssCode());
        writeToFile(baseDirPath, "index.js", multiFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     * @param bizType 业务类型
     * @return 唯一目录路径
     */
    private static String buildUniquePath(String bizType){
        String uniquePath = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextId());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniquePath;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
    /**
     * 写入单个文件
     *
     * @param dirPath  目录路径
     * @param fileName  文件名
     * @param content  文件内容
     */
    private static void writeToFile(String dirPath, String fileName, String content){
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}

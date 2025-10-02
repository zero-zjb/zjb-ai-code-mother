package com.zjb.zjbaicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.zjb.zjbaicodemother.exception.BusinessException;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Description: 抽象代码文件保存器模板 - 模板方法模式
 * @Author: zjb
 */
public abstract class CodeFileSaverTemplate<T> {

    //文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    public final File saveCode(T result) {
        // 1. 验证参数
        validateInput(result);
        // 2. 创建唯一目录
        String baseDirPath = buildUniquePath();
        // 3. 保存文件
        saveFiles(result, baseDirPath);
        // 4. 返回目录文件对象
        return new File(baseDirPath);
    }

    /**
     * 验证输入参数（可由子类覆盖）
     *
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        if (result == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 构建唯一目录路径（不能子类覆盖）
     *
     * @return 唯一目录路径
     */
    protected final String buildUniquePath() {
        String codeType = getCodeType().getValue();
        String uniquePath = StrUtil.format("{}_{}", codeType, IdUtil.getSnowflakeNextId());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniquePath;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件（不能子类覆盖）
     *
     * @param dirPath  目录路径
     * @param fileName  文件名
     * @param content  文件内容
     */
    protected final void writeToFile(String dirPath, String fileName, String content) {
        if(StrUtil.isNotBlank(content)){
            String filePath = dirPath + File.separator + fileName;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取代码类型（由子类实现）
     *
     * @return 代码类型枚举
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件（由子类实现）
     *
     * @param result    代码结果对象
     * @param baseDirPath   目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);
}

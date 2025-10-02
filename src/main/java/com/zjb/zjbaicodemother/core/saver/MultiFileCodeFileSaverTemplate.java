package com.zjb.zjbaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.zjb.zjbaicodemother.ai.model.MultiFileCodeResult;
import com.zjb.zjbaicodemother.exception.BusinessException;
import com.zjb.zjbaicodemother.exception.ErrorCode;
import com.zjb.zjbaicodemother.model.enums.CodeGenTypeEnum;
/**
 * @Description: 多文件代码保存模板
 * @Author: zjb
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        // 保存html文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        // 保存css文件
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        // 保存js文件
        writeToFile(baseDirPath, "script.js", result.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        // 校验输入
        if(StrUtil.isBlank(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "html代码内容不能为空");
        }
    }
}

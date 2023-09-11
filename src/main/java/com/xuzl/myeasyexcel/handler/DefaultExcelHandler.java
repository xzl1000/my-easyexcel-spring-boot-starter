package com.xuzl.myeasyexcel.handler;

import com.xuzl.myeasyexcel.common.EXTExecuteParam;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName DefaultExcelHandler.java
 * @Description TODO 默认实现
 * @createTime 2023-07-18 16:33
 */
public class DefaultExcelHandler<T> implements EXTExcelHandler<T>{
    @Override
    public String fileName(EXTExecuteParam<T> param) {
        return null;
    }

    @Override
    public String templateName(EXTExecuteParam<T> param) {
        return null;
    }
}

package com.xuzl.myeasyexcel.handler;

import com.xuzl.myeasyexcel.common.EXTExecuteParam;

import java.util.List;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName DefaultExcelSheetHandler.java
 * @Description TODO
 * @createTime 2023-07-18 16:35
 */
public class DefaultExcelSheetHandler<T> implements EXTExcelSheetHandler<T> {
    @Override
    public String sheetName(EXTExecuteParam<T> param) {
        return null;
    }

    @Override
    public List<List<String>> head(EXTExecuteParam<T> param) {
        return null;
    }

    @Override
    public Class<?> headClass(EXTExecuteParam<T> param) {
        return null;
    }
}

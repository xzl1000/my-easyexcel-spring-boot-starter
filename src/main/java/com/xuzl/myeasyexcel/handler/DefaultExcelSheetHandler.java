package com.xuzl.myeasyexcel.handler;


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
    public String sheetName(T param) {
        return null;
    }

    @Override
    public List<List<String>> head(T param) {
        return null;
    }

    @Override
    public Class<?> headClass(T param) {
        return null;
    }
}

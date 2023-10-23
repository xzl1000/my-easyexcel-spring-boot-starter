package com.xuzl.myeasyexcel.handler;


/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName DefaultExcelHandler.java
 * @Description TODO 默认实现
 * @createTime 2023-07-18 16:33
 */
public class DefaultExcelHandler<T> implements EXTExcelHandler<T>{
    @Override
    public String fileName(T param) {
        return null;
    }

    @Override
    public String templateName(T param) {
        return null;
    }
}

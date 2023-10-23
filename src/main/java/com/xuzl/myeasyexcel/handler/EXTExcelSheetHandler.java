package com.xuzl.myeasyexcel.handler;


import java.util.List;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcelSheetHandler.java
 * @Description TODO
 * @createTime 2023-07-01 15:10
 */
public interface EXTExcelSheetHandler<T> {
    /**
     * 动态sheet名称
     * @return
     */
    String sheetName(T param);

    /**
     * 自定义表头
     * @return
     */
    List<List<String>> head(T param);

    /**
     * 动态表头类
     */
    Class<?> headClass(T param);


}

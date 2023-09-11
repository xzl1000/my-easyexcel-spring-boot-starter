package com.xuzl.myeasyexcel.handler;


import com.xuzl.myeasyexcel.common.EXTExecuteParam;

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
    String sheetName(EXTExecuteParam<T> param);

    /**
     * 自定义表头
     * @return
     */
    List<List<String>> head(EXTExecuteParam<T> param);

    /**
     * 动态表头类
     */
    Class<?> headClass(EXTExecuteParam<T> param);


}

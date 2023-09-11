package com.xuzl.myeasyexcel.handler;

import com.xuzl.myeasyexcel.common.EXTExecuteParam;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcelHandler.java
 * @Description TODO
 * @createTime 2023-07-01 14:35
 */
public interface EXTExcelHandler<T> {

    /**
     * 动态文件名称
     * @param param
     * @return
     */
    String fileName(EXTExecuteParam<T> param);

    /**
     * 动态模板名称
     * @param param
     * @return
     */
    String templateName(EXTExecuteParam<T> param);



}

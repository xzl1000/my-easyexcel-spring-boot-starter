package com.xuzl.myeasyexcel.register;


import com.xuzl.myeasyexcel.config.ExecutorProperties;
import com.xuzl.myeasyexcel.executor.EXTExcelBaseExecutor;

import java.util.HashMap;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ExtExcelRegister.java
 * @Description TODO
 * @createTime 2023-10-12 10:51
 */
public interface ExtExcelRegister {
    /**
     * 注册执行器
     * @return
     */
    HashMap<Object, EXTExcelBaseExecutor<?>> register(ExecutorProperties executorProperties);
}

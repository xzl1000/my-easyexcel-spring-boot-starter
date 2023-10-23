package com.xuzl.myeasyexcel.register;


import com.xuzl.myeasyexcel.config.ExecutorProperties;
import com.xuzl.myeasyexcel.executor.IMTBaseExecutor;

import java.util.HashMap;


/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ImtExcelRegister.java
 * @Description TODO
 * @createTime 2023-10-12 11:29
 */
public interface ImtExcelRegister {
    /**
     * 注册执行器
     * @return
     */
    HashMap<Object, IMTBaseExecutor<?,?>> register(ExecutorProperties executorProperties);
}

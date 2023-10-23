package com.xuzl.myeasyexcel.config;


import com.xuzl.myeasyexcel.executor.EXTExcelBaseExecutor;
import com.xuzl.myeasyexcel.executor.IMTBaseExecutor;

import java.util.HashMap;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ExecutorGlobalContext.java
 * @Description TODO
 * @createTime 2023-10-12 11:23
 */
public class ExecutorGlobalContext {

    /**
     * 导出excel执行器
     */
    private HashMap<Object, EXTExcelBaseExecutor<?>> extExcelExecutorMap;
    /**
     * 导入执行器
     */
    private HashMap<Object, IMTBaseExecutor<?,?>> imtExecutorMap;

    public ExecutorGlobalContext(HashMap<Object, EXTExcelBaseExecutor<?>> extExcelExecutorMap, HashMap<Object, IMTBaseExecutor<?, ?>> imtExecutorMap) {
        this.extExcelExecutorMap = extExcelExecutorMap;
        this.imtExecutorMap = imtExecutorMap;
    }

    public HashMap<Object, EXTExcelBaseExecutor<?>> getExtExcelExecutorMap() {
        return extExcelExecutorMap;
    }

    public void setExtExcelExecutorMap(HashMap<Object, EXTExcelBaseExecutor<?>> extExcelExecutorMap) {
        this.extExcelExecutorMap = extExcelExecutorMap;
    }

    public HashMap<Object, IMTBaseExecutor<?, ?>> getImtExecutorMap() {
        return imtExecutorMap;
    }

    public void setImtExecutorMap(HashMap<Object, IMTBaseExecutor<?, ?>> imtExecutorMap) {
        this.imtExecutorMap = imtExecutorMap;
    }
}

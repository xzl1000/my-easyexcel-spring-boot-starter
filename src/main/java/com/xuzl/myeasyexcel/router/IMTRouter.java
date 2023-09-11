package com.xuzl.myeasyexcel.router;


import com.xuzl.myeasyexcel.common.IMTExecuteResult;
import com.xuzl.myeasyexcel.common.ImtSubmit;
import com.xuzl.myeasyexcel.executor.IMTBaseExecutor;

import java.util.Map;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTRouter.java
 * @Description TODO
 * @createTime 2023-05-17 11:03
 */
public class IMTRouter {
    private final Map<String, IMTBaseExecutor<?>> executorMap;

    public IMTRouter(Map<String, IMTBaseExecutor<?>> executorMap) {
        this.executorMap = executorMap;
    }

    public IMTExecuteResult router(ImtSubmit importRequestVO) {

        IMTBaseExecutor<?> executor = executorMap.get(importRequestVO.getExecutorName());

        if (executor == null) {
            throw new RuntimeException("can not find executor for executorName: " + importRequestVO.getExecutorName() + " !");
        }

        return executor.process(importRequestVO);
    }
}

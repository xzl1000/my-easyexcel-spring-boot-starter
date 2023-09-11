package com.xuzl.myeasyexcel.router;



import com.xuzl.myeasyexcel.common.ExtSubmit;
import com.xuzl.myeasyexcel.executor.EXTExcelBaseExecutor;

import java.util.Map;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTPdfRouter.java
 * @Description TODO
 * @createTime 2023-06-25 14:59
 */
public class EXTExcelRouter {
    private final Map<String, EXTExcelBaseExecutor<?>> executorMap;

    public EXTExcelRouter(Map<String, EXTExcelBaseExecutor<?>> executorMap) {
        this.executorMap = executorMap;
    }

    public String router(ExtSubmit requestVO) {
        if (requestVO == null || requestVO.getExecutorName() == null) {
            throw new RuntimeException("request or executorName can not be null !");
        }

        EXTExcelBaseExecutor<?> executor = executorMap.get(requestVO.getExecutorName());

        if (executor == null) {
            throw new RuntimeException("can not find executor for executorName: " + requestVO.getExecutorName() + " !");
        }

        return executor.process(requestVO);
    }
}

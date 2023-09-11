package com.xuzl.myeasyexcel.common;

import java.util.List;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName InvokerResult.java
 * @Description TODO
 * @createTime 2023-05-25 11:45
 */
public class InvokerResult {
    private boolean success;
    private List<BaseModel> failList;

    private InvokerResult(boolean success, List<BaseModel> failList) {
        this.success = success;
        this.failList = failList;
    }

    private InvokerResult(boolean success) {
        this(success,null);
    }

    public static InvokerResult result(boolean success) {
        return new InvokerResult(success);
    }

    public static InvokerResult result(boolean success, List<BaseModel> failList) {
        return new InvokerResult(success,failList);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<BaseModel> getFailList() {
        return failList;
    }

    public void setFailList(List<BaseModel> failList) {
        this.failList = failList;
    }
}

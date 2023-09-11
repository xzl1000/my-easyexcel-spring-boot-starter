package com.xuzl.myeasyexcel.common;


import java.io.Serializable;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ExtSubmit.java
 * @Description TODO
 * @createTime 2023-07-06 10:26
 */
public class ExtSubmit implements Serializable {
    private String executorName;

    private String params;

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}

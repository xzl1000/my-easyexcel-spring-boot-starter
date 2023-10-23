package com.xuzl.myeasyexcel.common;


import java.io.Serializable;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExecuteParam.java
 * @Description TODO
 * @createTime 2023-06-28 10:24
 */
public class EXTExecuteParam<T> implements Serializable {
    /**
     * 前端请求参数
     */
    private T params;

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

}

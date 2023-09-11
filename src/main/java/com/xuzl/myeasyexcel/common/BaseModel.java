package com.xuzl.myeasyexcel.common;

import com.alibaba.excel.annotation.ExcelIgnore;

import java.io.Serializable;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName BaseModel.java
 * @Description TODO
 * @createTime 2023-05-25 11:42
 */
public class BaseModel implements Serializable {
    @ExcelIgnore
    public String failReason;
    @ExcelIgnore
    public Integer rowIndex;

    public Integer getRowIndex() {
        return rowIndex;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setRowIndex(Integer rowIndex) {


        this.rowIndex = rowIndex;
    }

}

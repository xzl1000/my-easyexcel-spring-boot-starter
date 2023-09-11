package com.xuzl.myeasyexcel.common;

import com.alibaba.excel.metadata.data.CellData;

import java.io.Serializable;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName FailRowInfo.java
 * @Description TODO
 * @createTime 2023-05-18 16:12
 */
public class FailRowInfo implements Serializable {
    private Long rowNum;
    /**
     * 列号
     */
    private Long columnNum;

    /**
     * 失败原因
     */
    private String failReason;

    private CellData<?> cellData;

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    public Long getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(Long columnNum) {
        this.columnNum = columnNum;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public CellData<?> getCellData() {
        return cellData;
    }

    public void setCellData(CellData<?> cellData) {
        this.cellData = cellData;
    }
}

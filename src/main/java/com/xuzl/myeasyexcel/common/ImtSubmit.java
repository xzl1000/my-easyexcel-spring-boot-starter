package com.xuzl.myeasyexcel.common;


import java.io.Serializable;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ImtSubmitRequestVO.java
 * @Description TODO
 * @createTime 2023-05-16 11:04
 */
public class ImtSubmit implements Serializable {
    private String taskDesc;
    private String fileName;
    private String filePath;

    private String executorName;

    private String params;

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

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

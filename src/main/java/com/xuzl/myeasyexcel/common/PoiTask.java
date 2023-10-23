package com.xuzl.myeasyexcel.common;

import java.util.Date;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName PoiTask.java
 * @Description TODO
 * @createTime 2023-10-09 18:14
 */
public class PoiTask<T> {
    /**
     *
     */
    private Long taskId;

    private String taskDesc;

    private Long executorId;

    /**
     *
     */
    private String executorName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 状态：1-等待，2-进行中，3-完成，99-取消
     */
    private Long status;

    /**
     * 导入总条数
     */
    private Long totalNum;

    /**
     * 导入成功总条数
     */
    private Long successNum;

    /**
     * 失败总条数
     */
    private Long failNum;

    /**
     * 错误提示信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private Date createTime;

    private Integer type;
    /**
     * 扩展字段
     */
    private T extend;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Long getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Long successNum) {
        this.successNum = successNum;
    }

    public Long getFailNum() {
        return failNum;
    }

    public void setFailNum(Long failNum) {
        this.failNum = failNum;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public T getExtend() {
        return extend;
    }

    public void setExtend(T extend) {
        this.extend = extend;
    }
}

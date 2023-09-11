package com.xuzl.myeasyexcel.common;


import java.io.Serializable;
import java.util.List;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTExecuteResult.java
 * @Description TODO
 * @createTime 2023-05-16 17:22
 */
public class IMTExecuteResult implements Serializable {
    private Boolean shouldGoOn;
    private Long successCount;

    private Long failCount;

    private Long taskId;

    private List<BaseModel> failRows;

    public Boolean getShouldGoOn() {
        return shouldGoOn;
    }

    public void setShouldGoOn(Boolean shouldGoOn) {
        this.shouldGoOn = shouldGoOn;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public List<BaseModel> getFailRows() {
        return failRows;
    }

    public void setFailRows(List<BaseModel> failRows) {
        this.failRows = failRows;
    }

    public IMTExecuteResult() {
    }

    private IMTExecuteResult(Long successCount, Long failCount, List<BaseModel> failRows, Long taskId, Boolean shouldGoOn) {
        this.successCount = successCount;
        this.failCount = failCount;
        this.failRows = failRows;
        this.taskId = taskId;
        this.shouldGoOn = shouldGoOn;
    }

    public static IMTExecuteResult submitResult(Long taskId) {
        return new IMTExecuteResult(0L, 0L, null, taskId, true);
    }

    public static IMTExecuteResult success() {
        return success(0L, 0L);
    }

    public static IMTExecuteResult success(Long successCount, List<BaseModel> failRows){
        Long failCount = (long) failRows.size();
        return success(successCount, failCount, failRows);
    }

    public static IMTExecuteResult success(Long successCount, Long failCount) {
        return success(successCount, failCount, null);
    }

    public static IMTExecuteResult success(Long successCount, Long failCount, List<BaseModel> failRows) {
        return new IMTExecuteResult(successCount, failCount, failRows,null,true);
    }

    public static IMTExecuteResult fail() {
        return fail(0L, 0L);
    }

    public static IMTExecuteResult fail(Long successCount, List<BaseModel> failRows){
        Long failCount = (long) failRows.size();
        return fail(successCount, failCount, failRows);
    }

    public static IMTExecuteResult fail(Long successCount, Long failCount) {
        return fail(successCount, failCount, null);
    }

    public static IMTExecuteResult fail(Long successCount, Long failCount, List<BaseModel> failRows) {
        return new IMTExecuteResult(successCount, failCount, failRows,null,false);
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }
}

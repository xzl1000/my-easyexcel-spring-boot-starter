package com.xuzl.myeasyexcel.common;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ImtExcelProperties.java
 * @Description TODO
 * @createTime 2023-10-12 15:16
 */
public class ImtExcelProperties {
    // 业务名称（唯一，默认类名）
    private String executorName;

    /**
     * 单次循环行数
     */
    private int loopRowNumber;

    /**
     * 开始行数
     */
    private int startRowNumber;

    /**
     * 1-异步，2-同步
     */
    private int type;

    /**
     * 任务最大线程数
     */
    private int maxActiveThread;

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public int getLoopRowNumber() {
        return loopRowNumber;
    }

    public void setLoopRowNumber(int loopRowNumber) {
        this.loopRowNumber = loopRowNumber;
    }

    public int getStartRowNumber() {
        return startRowNumber;
    }

    public void setStartRowNumber(int startRowNumber) {
        this.startRowNumber = startRowNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMaxActiveThread() {
        return maxActiveThread;
    }

    public void setMaxActiveThread(int maxActiveThread) {
        this.maxActiveThread = maxActiveThread;
    }
}

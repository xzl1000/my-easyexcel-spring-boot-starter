package com.xuzl.myeasyexcel.executor;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelRuntimeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xuzl.myeasyexcel.common.*;
import com.xuzl.myeasyexcel.listener.AsyncReadListener;
import com.xuzl.myeasyexcel.listener.SyncReadListener;
import com.xuzl.myeasyexcel.utils.TaskIdUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTBaseExecutor.java
 * @Description TODO
 * @createTime 2023-05-16 10:40
 */
public abstract class IMTBaseExecutor<T extends BaseModel,R> {

    private ImtExcelProperties imtProperty;

    private MessageSource messageSource;

    private ThreadPoolExecutor threadPoolExecutor;

    private ConcurrentHashMap<Long, PoiTask<R>> taskMap = new ConcurrentHashMap<>();

    private Class<T> tClazz;

    private Class<R> rClazz;

    public final IMTExecuteResult process(ImtSubmit requestVO) {

        int loopCount = imtProperty.getLoopRowNumber();

        int startRow = imtProperty.getStartRowNumber();

        PoiTask<R> task = transRequest2Task(requestVO);

        // 1.1 任务类型。同步还是异步，同步直接进行，异步需要开启线程池
        if (POICodeConstants.IMT_JOB_TYPE_SYNC == imtProperty.getType()) {
            // 成功数
            AtomicLong successNum = new AtomicLong(0);
            // 失败数
            AtomicLong failNum = new AtomicLong(0);
            try {
                EasyExcel.read(requestVO.getFilePath(), tClazz, new SyncReadListener<T>(dataList ->
                {

                    IMTExecuteResult result = execute(dataList, task);
                    successNum.addAndGet(result.getSuccessCount());
                    failNum.addAndGet(result.getFailCount());

                },
                        failNum::addAndGet,
                        loopCount,
                        messageSource,
                        LocaleContextHolder.getLocale(),
                        tClazz)).sheet().headRowNumber(startRow).doRead();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            return IMTExecuteResult.success(successNum.get(), failNum.get());

        } else if (POICodeConstants.IMT_JOB_TYPE_ASYNC == imtProperty.getType()){
            // 保存任务信息
            taskMap.put(task.getTaskId(),task);
            // 创建任务钩子
            createTaskHook(task);
            // 放入
            threadPoolExecutor.execute(new IMTThread(task.getTaskId()));
            return IMTExecuteResult.submitResult(task.getTaskId());
        }
        return new IMTExecuteResult();
    }

    private class IMTThread implements Runnable{
        private final Long taskId;

        public IMTThread(Long taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            // TODO 执行导入任务 1. 读取文件 2. 执行导入任务 3. 更新任务状态

            PoiTask<R> task = taskMap.get(taskId);

            String filePath = task.getFilePath();

            int loopCount = imtProperty.getLoopRowNumber(); // 默认1000

            int startRow = imtProperty.getStartRowNumber(); // 默认从第二行开始

            // 总行数
            AtomicLong totalNum = new AtomicLong(0);
            // 总成功数
            AtomicLong successNum = new AtomicLong(0);
            // 总失败数
            AtomicLong failNum = new AtomicLong(0);

            // 更新任务状态为执行中
            updateTaskStatus(taskId,POICodeConstants.IMT_TASK_STATUS_RUNNING);

            // 读取文件
            try {
                EasyExcel.read(filePath, tClazz, new AsyncReadListener<T>(dataList -> {
                    // 如果任务状态为取消，则结束任务
                    if (POICodeConstants.IMT_TASK_STATUS_CANCEL.equals(task.getStatus())) {
                        return InvokerResult.result(false);
                    }
                    // 执行导入任务
                    IMTExecuteResult result = execute(dataList, task);

                    // 保存失败条目
                    List<FailRow> failRows = new ArrayList<>();
                    if (result.getFailRows()!=null && result.getFailRows().size() > 0) {
                        for (BaseModel failRow : result.getFailRows()) {
                            FailRow failRowEntity = new FailRow();
                            failRowEntity.setTaskId(taskId);//任务id
                            failRowEntity.setRowNum(failRow.rowIndex.longValue());
                            failRowEntity.setFailReason(failRow.failReason);
                            failRows.add(failRowEntity);
                        }
                    }

                    successNum.addAndGet(result.getSuccessCount());
//                    failNum.addAndGet(result.getFailCount());

                    // 更新任务信息
                    PoiTask<R> poiTask = updateTaskResult(taskId, successNum.get(), failNum.get(), totalNum.get());
                    syncTaskHook(poiTask);
                    // 休眠，释放CPU
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // 是否继续执行
                    return InvokerResult.result(result.getShouldGoOn(), failRows);
                }, failRowInfoList -> {
                    // 失败条目
                    try {
//                        failRowInfoList.forEach(e->{
//                            // 保存失败条目
//                            TImtFailRows failRow = new TImtFailRows();
//                            failRow.setTaskId(tImtTask.getTaskId());
//                            failRow.setRowNum(e.getRowNum());
//                            failRow.setColumnNum(e.getColumnNum());
//                            failRow.setFailReason("第{"+translateIndexToExcelIndex(e.getColumnNum())+"}列解析异常，请检查数据格式");
//                            failRows.add(failRow);
//                        });

                        // 处理失败条目
                        failRowsHook(failRowInfoList,task);

                        failNum.addAndGet(failRowInfoList.size());

                        // 更新任务信息
                        PoiTask<R> poiTask = updateTaskResult(taskId, successNum.get(), failNum.get(), totalNum.get());
                        syncTaskHook(poiTask);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                        totalNum::set,
                        loopCount,
                        messageSource,
                        LocaleContextHolder.getLocale(),
                        tClazz)).sheet().headRowNumber(startRow).doRead();
                // 任务完成，在map中移除，触发钩子
                finishTaskHook(task);
            } catch (Exception e) {
                dealExceptionOnRead(task, e);
            } finally {
                taskMap.remove(taskId);
            }
        }
    }

    /**
     * easyExcel列下标转换为excel列下标
     * @param index
     * @return
     */
    private static String translateIndexToExcelIndex(long index){
        int a = (int) (index / 26);
        int b = (int) (index % 26);
        if (a == 0) {
            return String.valueOf((char) (b + 65));
        } else {
            return String.valueOf((char) (a + 64)) + (char) (b + 65);
        }
    }

    /**
     * 异步读取异常处理
     * @param task
     * @param e
     */
    private void dealExceptionOnRead(PoiTask<R> task, Exception e) {
        e.printStackTrace();
        // 更新任务状态为异常
        task.setStatus(POICodeConstants.IMT_TASK_STATUS_EXCEPTION);
        if (e instanceof ExcelRuntimeException){
            task.setErrorMessage(e.getMessage());
        }else {
            task.setErrorMessage("导入任务执行发生异常，请联系管理员！");
        }
        syncTaskHook(task);
        taskMap.put(task.getTaskId(),task);
    }


    /**
     * 保存任务信息
     * @param requestVO
     * @return
     */
//    private PoiTask saveTaskInfo(PoiTask requestVO){
//        TImtTask tImtTask = new TImtTask();
//        tImtTask.setTaskId(TaskIdUtil.nextId());
//        tImtTask.setTaskDesc(requestVO.getTaskDesc());
//        tImtTask.setCreateTime(DateUtils.getSystemDate());
//        tImtTask.setBranchId(requestVO.getBranchId());
//        tImtTask.setBranchName(requestVO.getBranchName());
//        tImtTask.setExecutorId(requestVO.getExecutorId());
//        tImtTask.setDeptCode(requestVO.getDeptCode());
//        tImtTask.setDeptName(requestVO.getDeptName());
//        tImtTask.setFilePath(requestVO.getFilePath());
//        tImtTask.setFileName(requestVO.getFileName());
//        tImtTask.setCreateId(requestVO.getCreateId());
//        tImtTask.setCreateName(requestVO.getCreateName());
//        tImtTask.setExtend(requestVO.getExtend());
//        tImtTask.setTotalNum(0L);
//        tImtTask.setSuccessNum(0L);
//        tImtTask.setFailNum(0L);
//        tImtTask.setIsValid(CodeConstants.SYS_STATUS_YES);
//        tImtTask.setStatus(POICodeConstants.IMT_TASK_STATUS_WAIT);
//        int insert = tImtTaskMapper.insert(tImtTask);
//        if (insert <= 0){
//            throw new RuntimeException("保存任务信息失败");
//        }
//        return tImtTask;
//    }

    /**
     *
     * @param submit
     * @return
     */
    private PoiTask<R> transRequest2Task(ImtSubmit submit){
        PoiTask<R> task = new PoiTask<>();
        task.setTaskDesc(submit.getTaskDesc());
        task.setExecutorName(submit.getExecutorName());
        task.setExecutorId(submit.getExecutorId());
        task.setFilePath(submit.getFilePath());
        task.setFileName(submit.getFileName());
        task.setCreateTime(new Date());
        task.setFailNum(0L);
        task.setSuccessNum(0L);
        task.setTotalNum(0L);
        task.setStatus(POICodeConstants.IMT_TASK_STATUS_WAIT);
        task.setTaskId(TaskIdUtil.nextId());
        if (getTypeReference()==null){
            task.setExtend(JSON.parseObject(submit.getParams(), rClazz));
        }else {
            task.setExtend(JSON.parseObject(submit.getParams(), getTypeReference()));
        }
        return task;
    }

    public abstract TypeReference<R> getTypeReference();

    public ImtExcelProperties getImtProperty() {
        return imtProperty;
    }

    /**
     * 更新任务状态
     * @param taskId
     * @param status
     * @return
     */
    private PoiTask<R> updateTaskStatus(Long taskId, Long status){
        return taskMap.compute(taskId,(k,v)->{
            if (v == null){
                return null;
            }
            v.setStatus(status);
            return v;
        });
    }

    /**
     * 更新任务结果
     * @param taskId
     * @param successNum
     * @param failNum
     * @return
     */
    private PoiTask<R> updateTaskResult(Long taskId, Long successNum, Long failNum, Long totalNum){
        return taskMap.compute(taskId,(k,v)->{
            if (v == null){
                return null;
            }
            if (successNum != null){
                v.setSuccessNum(successNum);
            }
            if (failNum != null){
                v.setFailNum(failNum);
            }
            if (totalNum != null){
                v.setTotalNum(totalNum);
            }
            return v;
        });
    }

    /**
     * 获取任务信息
     * @param taskId
     * @return
     */
    public PoiTask<R> getTask(Long taskId){
        return taskMap.get(taskId);
    }

    /**
     * 取消任务
     * @param taskId
     * @return
     */
    public boolean cancelTask(Long taskId){
        PoiTask<R> task = updateTaskStatus(taskId, POICodeConstants.IMT_TASK_STATUS_CANCEL);
        return task != null;
    }

    /**
     * @param dataList
     * @param task
     * @return
     */
    public abstract IMTExecuteResult execute(List<T> dataList, PoiTask<R> task);

    /**
     * 失败条目钩子
     * @param dataList
     * @param task
     */
    public void failRowsHook(List<FailRow> dataList, PoiTask<R> task){}

    /**
     * 结束任务钩子
     * @param task
     */
    public void finishTaskHook(PoiTask<R> task){}
    /**
     * 创建任务钩子
     * @param task
     */
    public void createTaskHook(PoiTask<R> task){}

    /**
     * 同步任务钩子
     * @param task
     */
    public void syncTaskHook(PoiTask<R> task){}
}

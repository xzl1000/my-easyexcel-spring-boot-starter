package com.xuzl.myeasyexcel.executor;


import com.alibaba.excel.EasyExcel;
import com.xuzl.myeasyexcel.annotation.IMTExcel;
import com.xuzl.myeasyexcel.common.BaseModel;
import com.xuzl.myeasyexcel.common.IMTExecuteResult;
import com.xuzl.myeasyexcel.common.ImtSubmit;
import com.xuzl.myeasyexcel.common.POICodeConstants;
import com.xuzl.myeasyexcel.listener.SyncReadListener;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTBaseExecutor.java
 * @Description TODO
 * @createTime 2023-05-16 10:40
 */
public abstract class IMTBaseExecutor<T extends BaseModel> {

    private IMTExcel imtProperty;

    private MessageSource messageSource;

    private Class<T> clazz;


    public final IMTExecuteResult process(ImtSubmit requestVO) {

        int loopCount = imtProperty.loopRowNumber();

        int startRow = imtProperty.startRowNumber();

        // 1.1 任务类型。同步还是异步，同步直接进行，异步需要开启线程池
        if (POICodeConstants.IMT_JOB_TYPE_SYNC == imtProperty.type()) {
            // 成功数
            AtomicLong successNum = new AtomicLong(0);
            // 失败数
            AtomicLong failNum = new AtomicLong(0);
            try {
                EasyExcel.read(requestVO.getFilePath(), clazz, new SyncReadListener<>(dataList -> {

                    IMTExecuteResult result = execute(dataList, requestVO);
                    successNum.addAndGet(result.getSuccessCount());
                    failNum.addAndGet(result.getFailCount());

                }, failNum::addAndGet, loopCount, messageSource, LocaleContextHolder.getLocale(), clazz)).sheet().headRowNumber(startRow).doRead();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return IMTExecuteResult.success(successNum.get(), failNum.get());

        }
        return new IMTExecuteResult();
//        else if (POICodeConstants.IMT_JOB_TYPE_ASYNC.equals(tImtExecutor.getType())){
//            // 保存任务信息
//            TImtTask tImtTask = saveTaskInfo(requestVO);
//            // 放入等待队列
//            threadPoolExecutor.execute(new IMTThread(tImtTask, tImtExecutor,clazz));
//            // 线程参数是否发生变化
//            if (threadPoolExecutor.getCorePoolSize()!= coreSize){
//                threadPoolExecutor.setCorePoolSize(coreSize);
//            }
//            return IMTExecuteResult.submitResult(tImtTask.getTaskId());
//        }
//        return new IMTExecuteResult();
    }
//
//    private class IMTThread implements Runnable{
//        private final TImtTask tImtTask;
//
//        private final TImtExecutor tImtExecutor;
//
//        private final Class<?> clazz;
//
//        public IMTThread(TImtTask tImtTask, TImtExecutor tImtExecutor, Class<?> clazz) {
//            this.tImtTask = tImtTask;
//            this.tImtExecutor = tImtExecutor;
//            this.clazz = clazz;
//        }
//
//        @Override
//        public void run() {
//            // TODO 执行导入任务 1. 读取文件 2. 执行导入任务 3. 更新任务状态
//            log.info("--------------------异步导入任务开始执行，任务ID：{}", tImtTask.getTaskId());
//
//            String filePath = tImtTask.getFilePath();
//
//            int loopCount = tImtExecutor.getLoopRowNumber() != null? tImtExecutor.getLoopRowNumber().intValue():POICodeConstants.DEFAULT_BATCH_COUNT; // 默认100
//
//            int startRow = tImtExecutor.getStartRowNumber() != null? tImtExecutor.getStartRowNumber().intValue():POICodeConstants.DEFAULT_START_ROW; // 默认从第二行开始
//
//            // 总行数
//            AtomicLong totalNum = new AtomicLong(0);
//            // 总成功数
//            AtomicLong successNum = new AtomicLong(0);
//            // 总失败数
//            AtomicLong failNum = new AtomicLong(0);
//
//            // 读取文件
//            try {
//                EasyExcel.read(filePath, clazz, new AsyncReadListener<T>(dataList -> {
//                    // 检查任务状态
//                    TImtTask oriImtTask = tImtTaskMapper.selectById(tImtTask.getTaskId());
//                    // 如果任务状态为取消，则结束任务
//                    if (POICodeConstants.IMT_TASK_STATUS_CANCEL.equals(oriImtTask.getStatus())) {
//                        log.warn("--------------------导入任务已取消，中断读取");
//                        return InvokerResult.result(false);
//                    }
//                    // 执行导入任务
//                    IMTExecuteResult result = execute(dataList, tImtTask);
//
//                    // 保存失败条目
//                    List<TImtFailRows> failRows = new ArrayList<>();
//                    if (result.getFailRows()!=null && result.getFailRows().size() > 0) {
//                        for (BaseModel failRow : result.getFailRows()) {
//                            TImtFailRows failRowEntity = new TImtFailRows();
//                            failRowEntity.setFailId(SnowflakeUtil.nextId());
//                            failRowEntity.setTaskId(tImtTask.getTaskId());
//                            failRowEntity.setRowNum(failRow.rowIndex.longValue());
//                            failRowEntity.setFailReason(failRow.failReason);
//                            failRows.add(failRowEntity);
//                        }
//                        tImtFailRowsMapper.insertBatch(failRows);
//                    }
//
//                    successNum.addAndGet(result.getSuccessCount());
//                    failNum.addAndGet(result.getFailCount());
//
//                    // 更新任务信息
//                    tImtTaskMapper.updateRunningTask(tImtTask.getTaskId(),totalNum.get(),successNum.get(),failNum.get());
//
//                    // 休眠，释放CPU
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    // 是否继续执行
//                    return InvokerResult.result(result.getShouldGoOn(), result.getFailRows());
//                }, failRowInfoList -> {
//                    // 失败条目
//                    try {
//                        List<TImtFailRows> failRows = new ArrayList<>();
//                        failRowInfoList.forEach(e->{
//                            // 保存失败条目
//                            TImtFailRows failRow = new TImtFailRows();
//                            failRow.setFailId(SnowflakeUtil.nextId());
//                            failRow.setTaskId(tImtTask.getTaskId());
//                            failRow.setRowNum(e.getRowNum());
//                            failRow.setColumnNum(e.getColumnNum());
//                            failRow.setFailReason("第{"+translateIndexToExcelIndex(e.getColumnNum())+"}列解析异常，请检查数据格式");
//                            failRows.add(failRow);
//                        });
//                        // 保存失败条目
//                        tImtFailRowsMapper.insertBatch(failRows);
//
//                        failNum.addAndGet(failRows.size());
//
//                        // 更新任务信息
//                        tImtTaskMapper.updateRunningTask(tImtTask.getTaskId(),totalNum.get(),null,failNum.get());
//                    } catch (Exception e) {
//                        log.warn("--------------------导入任务失败条目保存失败，继续线程",e);
//                        e.printStackTrace();
//                    }
//                },totalNum::set,loopCount)).sheet().headRowNumber(startRow).doRead();
//            } catch (Exception e) {
//                dealExceptionOnRead(tImtTask, e);
//                return;
//            }
//            // 更新任务状态为完成
//            tImtTaskMapper.finishTask(tImtTask.getTaskId());
//            log.info("--------------------异步导入任务执行完成，任务ID：{}", tImtTask.getTaskId());
//        }
//    }
//
//    /**
//     * easyExcel列下标转换为excel列下标
//     * @param index
//     * @return
//     */
//    private static String translateIndexToExcelIndex(long index){
//        int a = (int) (index / 26);
//        int b = (int) (index % 26);
//        if (a == 0) {
//            return String.valueOf((char) (b + 65));
//        } else {
//            return String.valueOf((char) (a + 64)) + (char) (b + 65);
//        }
//    }
//
//    /**
//     * 异步读取异常处理
//     * @param tImtTask
//     * @param e
//     */
//    private void dealExceptionOnRead(TImtTask tImtTask, Exception e) {
//        log.error("--------------------导入任务执行异常，中断线程",e);
//        e.printStackTrace();
//        // 更新任务状态为异常
//        if (e instanceof ExcelRuntimeException || e instanceof AppException){
//            tImtTaskMapper.exceptionTask(tImtTask.getTaskId(),e.getMessage());
//        }else {
//            tImtTaskMapper.exceptionTask(tImtTask.getTaskId(),"导入任务执行发生异常，请联系管理员！");
//        }
//    }
//
//    /**
//     * 保存任务信息
//     * @param requestVO
//     * @return
//     */
//    private TImtTask saveTaskInfo(ImtSubmitRequestVO requestVO){
//        TImtTask tImtTask = new TImtTask();
//        tImtTask.setTaskId(SnowflakeUtil.nextId());
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
     * @param dataList
     * @param requestVO
     * @return
     */
    public abstract IMTExecuteResult execute(List<T> dataList, ImtSubmit requestVO);

}

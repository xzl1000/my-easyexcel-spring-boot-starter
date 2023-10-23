package com.xuzl.myeasyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.util.ListUtils;
import com.xuzl.myeasyexcel.common.BaseModel;
import com.xuzl.myeasyexcel.common.FailRow;
import com.xuzl.myeasyexcel.common.InvokerResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName AsyncReadListener.java
 * @Description TODO
 * @createTime 2023-05-17 18:17
 */
public class AsyncReadListener<T extends BaseModel> extends I18nAnalysisListener<T> {
    public static int BATCH_COUNT = 100;
    private List<T> cachedDataList;
    private List<FailRow> cachedFailList;
    private final Function<List<T>, InvokerResult> readFunction;

    private final Consumer<List<FailRow>> failConsumer;

    private final Consumer<Integer> progressConsumer;
    private final int batchCount;

    private int totalRowsCount = 0;
    private boolean keep = true;

    /**
     * 计数器用法
     * @param progressConsumer
     */
    public AsyncReadListener(Consumer<Integer> progressConsumer, MessageSource messageSource, Locale locale, Class<T> clazz) {
        this(null, null,progressConsumer, BATCH_COUNT, messageSource, locale, clazz);
    }

    /**
     * @param readFunction
     * @param batchCount
     */
    public AsyncReadListener(Function<List<T>, InvokerResult> readFunction, int batchCount, MessageSource messageSource, Locale locale, Class<T> clazz) {
        this(readFunction, null,null, batchCount, messageSource, locale, clazz);
    }

    /**
     * 异常处理
     * @param readFunction
     * @param failConsumer
     * @param batchCount
     */
    public AsyncReadListener(Function<List<T>,InvokerResult> readFunction, Consumer<List<FailRow>> failConsumer, int batchCount, MessageSource messageSource, Locale locale, Class<T> clazz) {
        this(readFunction, failConsumer,null, batchCount, messageSource, locale, clazz);
    }

    public AsyncReadListener(Function<List<T>,InvokerResult> readFunction, Consumer<List<FailRow>> failConsumer, Consumer<Integer> progressConsumer, int batchCount, MessageSource messageSource, Locale locale, Class<T> clazz) {
        super(messageSource, locale, clazz);
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        this.cachedFailList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        this.progressConsumer = progressConsumer;
        this.readFunction = readFunction;
        this.batchCount = batchCount;
        this.failConsumer = failConsumer;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {

        totalRowsCount =  Math.max(totalRowsCount, context.readRowHolder().getRowIndex()-context.readSheetHolder().getHeadRowNumber()+1);

        if (failConsumer!=null && exception instanceof ExcelDataConvertException) {
            FailRow failRowInfo = new FailRow();
            failRowInfo.setRowNum((long)((ExcelDataConvertException) exception).getRowIndex());
            failRowInfo.setColumnNum((long)((ExcelDataConvertException) exception).getColumnIndex());
            failRowInfo.setCellData(((ExcelDataConvertException) exception).getCellData());
            this.cachedFailList.add(failRowInfo);

            if (this.batchCount >0 &&this.cachedFailList.size() >= this.batchCount) {
                this.failConsumer.accept(this.cachedFailList);
                this.cachedFailList = ListUtils.newArrayListWithExpectedSize(this.batchCount);
            }
        }else {
            throw exception;
        }
    }

    public void invoke(T data, AnalysisContext context) {
        totalRowsCount =  Math.max(totalRowsCount, context.readRowHolder().getRowIndex()-context.readSheetHolder().getHeadRowNumber()+1);
        // 携带行数
        ReadRowHolder readRowHolder = context.readRowHolder();
        Integer rowIndex = readRowHolder.getRowIndex();
        data.setRowIndex(rowIndex);
        this.cachedDataList.add(data);
        if (this.batchCount > 0 && this.cachedDataList.size() >= this.batchCount) {
            if (this.progressConsumer!=null) {
                this.progressConsumer.accept(totalRowsCount);
            }
            InvokerResult result = this.readFunction.apply(this.cachedDataList);
            // 根据返回结果判断是否继续读取
            if (result != null) {
                if (!result.isSuccess()){
                    this.cancel();
                    return;
                }
                // 处理失败数据
                this.cachedFailList.addAll(result.getFailList());
                if (this.cachedFailList.size() >= this.batchCount) {
                    this.failConsumer.accept(this.cachedFailList);
                    this.cachedFailList = ListUtils.newArrayListWithExpectedSize(this.batchCount);
                }

            }
            this.cachedDataList = ListUtils.newArrayListWithExpectedSize(this.batchCount);
        }

    }

    public void doAfterAllAnalysed(AnalysisContext context) {
        if (readFunction!=null && CollectionUtils.isNotEmpty(this.cachedDataList)) {
            if (this.progressConsumer!=null) {
                this.progressConsumer.accept(totalRowsCount);
            }
            InvokerResult result = this.readFunction.apply(this.cachedDataList);
            if (!result.isSuccess()){
                return;
            }
            // 处理失败数据
            this.cachedFailList.addAll(result.getFailList());
        }
        if (failConsumer!=null && CollectionUtils.isNotEmpty(this.cachedFailList)) {
            this.failConsumer.accept(this.cachedFailList);
        }


    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return keep && super.hasNext(context);
    }

    /**
     * 取消读取
     */
    public void cancel() {
        this.keep = false;
    }
}

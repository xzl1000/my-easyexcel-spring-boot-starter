package com.xuzl.myeasyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.util.ListUtils;
import com.xuzl.myeasyexcel.common.BaseModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName AsyncReadListener.java
 * @Description TODO
 * @createTime 2023-05-17 18:17
 */
public class SyncReadListener<T extends BaseModel> extends I18nAnalysisListener<T> {
    public static int BATCH_COUNT = 100;
    private List<T> cachedDataList;
    private final Consumer<List<T>> readConsumer;

    private final Consumer<Integer> afterAllAnalysedConsumer;

    private final int batchCount;

    private int failRows = 0;

    public SyncReadListener(Consumer<List<T>> readConsumer, int batchCount, MessageSource messageSource, Locale locale, Class<T> clazz) {
        this(readConsumer, null, batchCount,messageSource, locale, clazz);
    }

    public SyncReadListener(Consumer<List<T>> readConsumer, Consumer<Integer> afterAllAnalysedConsumer, int batchCount, MessageSource messageSource, Locale locale, Class<T> clazz) {
        super(messageSource, locale, clazz);
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        this.readConsumer = readConsumer;
        this.afterAllAnalysedConsumer = afterAllAnalysedConsumer;
        this.batchCount = batchCount;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        if (exception instanceof ExcelDataConvertException) {
            exception.printStackTrace();
            failRows++;
        }
    }

    public void invoke(T data, AnalysisContext context) {
        // 携带行数
        ReadRowHolder readRowHolder = context.readRowHolder();
        Integer rowIndex = readRowHolder.getRowIndex();
        data.setRowIndex(rowIndex);
        this.cachedDataList.add(data);
        if (this.batchCount > 0 && this.cachedDataList.size() >= this.batchCount) {
            this.readConsumer.accept(this.cachedDataList);
            this.cachedDataList = ListUtils.newArrayListWithExpectedSize(this.batchCount);
        }
    }

    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(this.cachedDataList)) {
            this.readConsumer.accept(this.cachedDataList);
        }
        if (this.afterAllAnalysedConsumer != null) {
            this.afterAllAnalysedConsumer.accept(failRows);
        }
    }
}

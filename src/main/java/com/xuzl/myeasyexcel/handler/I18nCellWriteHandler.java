package com.xuzl.myeasyexcel.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.shuabao.foundation.utils.PlaceholderResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName I18nCellWriteHandler.java
 * @Description TODO
 * @createTime 2023-07-19 10:49
 */
public class I18nCellWriteHandler implements CellWriteHandler {
    private final MessageSource messageSource;

    private final Locale locale;

    public I18nCellWriteHandler(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            List<String> originHeadNames = head.getHeadNameList();
            if (CollectionUtils.isNotEmpty(originHeadNames)) {
                List<String> newHeadNames = originHeadNames.stream().
                        map(headName ->
                                PlaceholderResolver.getDefaultResolver().resolveByRule(headName,
                                        (name) -> messageSource.getMessage(name, null, locale))).
                        collect(Collectors.toList());
                head.setHeadNameList(newHeadNames);
            }
        }
    }

}

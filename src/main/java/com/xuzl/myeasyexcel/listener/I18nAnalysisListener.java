package com.xuzl.myeasyexcel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.StringUtils;
import com.xuzl.myeasyexcel.utils.PlaceholderResolver;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName I18nAnalysisListener.java
 * @Description TODO
 * @createTime 2023-07-19 10:54
 */
public abstract class I18nAnalysisListener<T> extends AnalysisEventListener<T> {
    private final MessageSource messageSource;

    private final Locale locale;

    private final Class<T> clazz;

    protected I18nAnalysisListener(MessageSource messageSource, Locale locale, Class<T> clazz) {
        this.messageSource = messageSource;
        this.locale = locale;
        this.clazz = clazz;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadRowHolder readRowHolder = context.readRowHolder();
        int rowIndex = readRowHolder.getRowIndex();
        int currentHeadRowNumber = context.readSheetHolder().getHeadRowNumber();
        if (currentHeadRowNumber == rowIndex + 1) {
            buildHeadAgain(context, headMap);
        }

        invokeHeadMap(ConverterUtils.convertToStringMap(headMap, context), context);
    }


    private void buildHeadAgain(AnalysisContext analysisContext, Map<Integer, ReadCellData<?>> headMap) {
        ExcelReadHeadProperty excelHeadPropertyData = analysisContext.readSheetHolder().excelReadHeadProperty();
        Map<Integer, Head> nowHeadMapData = excelHeadPropertyData.getHeadMap();
        // 如果 nowHeadMapData 不为空，说明头的顺序已经确定 ，不需要重新构建头
        if (MapUtils.isNotEmpty(nowHeadMapData)) {
            return;
        }
        // 框架层面把HeadMapData替换掉了，这里要重新解析拿到原始的 HeadMapData 和 ExcelContentProperty
        ExcelReadHeadProperty originExcelHeadPropertyData = new ExcelReadHeadProperty(analysisContext.currentReadHolder(), clazz,
                null);
        Map<Integer, Head> originHeadMapData = originExcelHeadPropertyData.getHeadMap();
        // 下面代码就是 copy的 com.alibaba.excel.read.processor.DefaultAnalysisEventProcessor#buildHead
        Map<Integer, String> dataMap = ConverterUtils.convertToStringMap(headMap, analysisContext);
        Map<Integer, Head> tmpHeadMap = new HashMap<>(originHeadMapData.size() * 4 / 3 + 1);
        for (Map.Entry<Integer, Head> entry : originHeadMapData.entrySet()) {
            Head headData = entry.getValue();
            List<String> headNameList = headData.getHeadNameList();
            String headName = PlaceholderResolver.getDefaultResolver().resolveByRule(headNameList.get(headNameList.size() - 1),
                    (name) -> messageSource.getMessage(name, null, locale));
            for (Map.Entry<Integer, String> stringEntry : dataMap.entrySet()) {
                if (stringEntry == null) {
                    continue;
                }
                String headString = stringEntry.getValue();
                Integer stringKey = stringEntry.getKey();
                if (StringUtils.isEmpty(headString)) {
                    continue;
                }
                if (analysisContext.currentReadHolder().globalConfiguration().getAutoTrim()) {
                    headString = headString.trim();
                }
                if (headName.equals(headString)) {
                    headData.setColumnIndex(stringKey);
                    tmpHeadMap.put(stringKey, headData);
                    break;
                }
            }
        }
        excelHeadPropertyData.setHeadMap(tmpHeadMap);
    }
}

package com.xuzl.myeasyexcel.executor;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.xuzl.myeasyexcel.annotation.EXTExcel;
import com.xuzl.myeasyexcel.annotation.EXTExcelSheet;
import com.xuzl.myeasyexcel.common.EXTExecuteParam;
import com.xuzl.myeasyexcel.common.ExtSubmit;
import com.xuzl.myeasyexcel.common.POICodeConstants;
import com.xuzl.myeasyexcel.handler.EXTExcelHandler;
import com.xuzl.myeasyexcel.handler.EXTExcelSheetHandler;
import com.xuzl.myeasyexcel.handler.I18nCellWriteHandler;
import com.xuzl.myeasyexcel.utils.FileUtil;
import com.xuzl.myeasyexcel.utils.PlaceholderResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcelBaseExecutor.java
 * @Description TODO
 * @createTime 2023-06-27 11:20
 */
public abstract class EXTExcelBaseExecutor<T> {
    private String rootPath;

    private EXTExcel excelProperties;

    private List<Method> sheetsMethods;

    private MessageSource messageSource;

    private Class<T> clazz;

    private Field pageNumberField;

    private Field pageSizeField;

    public String process(ExtSubmit requestVO) {
        if (excelProperties == null){
            throw new RuntimeException("can not find annotation for " + this.getClass() + " !");
        }
        Locale locale = LocaleContextHolder.getLocale();
        // 组装执行方法入参
        EXTExecuteParam<T> param = new EXTExecuteParam<>();
        param.setParams(JSON.parseObject(requestVO.getParams(), clazz));

        // 自定义拦截器
        Class<? extends EXTExcelHandler> handler = excelProperties.handler();
        EXTExcelHandler<T> extExcelHandler = null;
        try {
            extExcelHandler = handler.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String fileName = extExcelHandler.fileName(param);
        String templateName = extExcelHandler.templateName(param);

        // 拦截器自定义的名字不会修改
        String tempName = excelProperties.fileName();
        if (excelProperties.withDateSuffix()) {
            tempName += "_" + new SimpleDateFormat(excelProperties.suffixFormat()).format(new Date());
        }
        fileName = StringUtils.isEmpty(fileName) ? tempName : fileName;

        templateName = StringUtils.isEmpty(templateName) ? excelProperties.templateName():templateName;

        if (StringUtils.isEmpty(fileName)){
            throw new RuntimeException("fileName can not be null !");
        }
        // 文件名国际化
        String realName = PlaceholderResolver.getDefaultResolver().resolveByRule(fileName,
                (name) -> messageSource.getMessage(name, null, locale));

        String newFilePath = FileUtil.createNewXlsxFile(rootPath, realName);

        // 创建写工作簿对象
        ExcelWriterBuilder writerBuilder = EasyExcel.write(newFilePath).registerWriteHandler(new I18nCellWriteHandler(messageSource, locale));

        // 如果配置了模板，则使用模板
        if (!StringUtils.isEmpty(templateName)){
            String sourcePath = POICodeConstants.EXCEL_TEMPLATE_ROOT_PATH + templateName + POICodeConstants.XLSX_FILE_SUFFIX;
            writerBuilder = writerBuilder.withTemplate(this.getClass().getResourceAsStream(sourcePath));
        }

        try (ExcelWriter excelWriter = writerBuilder.build()) {

            // 遍历所有sheet
            int sheetIndex = 0;
            for (Method sheetsMethod : sheetsMethods) {

                EXTExcelSheet excelSheetProp = sheetsMethod.getDeclaredAnnotation(EXTExcelSheet.class);

                // 自定义拦截器
                Class<? extends EXTExcelSheetHandler> sheetHandler = excelSheetProp.handler();
                EXTExcelSheetHandler<T> extExcelSheetHandler = sheetHandler.newInstance();

                String sheetName = extExcelSheetHandler.sheetName(param);
                Class<?> headClass = extExcelSheetHandler.headClass(param);

                sheetName = StringUtils.isEmpty(sheetName) ? excelSheetProp.sheetName():sheetName;
                sheetName = StringUtils.isEmpty(sheetName) ? sheetsMethod.getName() : sheetName;

                // 文件名国际化
                String realSheetName = PlaceholderResolver.getDefaultResolver().resolveByRule(sheetName,
                        (name) -> messageSource.getMessage(name, null, locale));

                headClass = headClass == null ? excelSheetProp.headClass():headClass;

                // 创建sheet
                ExcelWriterSheetBuilder excelWriterSheetBuilder = EasyExcel.writerSheet(sheetIndex++, realSheetName);
                // 如果配置了表头，则使用表头
                if (headClass != null && !headClass.equals(Object.class)) {
                    excelWriterSheetBuilder = excelWriterSheetBuilder.head(headClass);
                }

                WriteSheet writeSheet = excelWriterSheetBuilder.build();

                int maxRowNum = excelSheetProp.maxRowNum();
                boolean autoSwitchSheet = excelSheetProp.autoSwitchSheet();
                int sheetTotalRowNum = 0;
                int autoSwitchSheetIndex = 1;

                int pageNumber = 1;
                int pageSize = Math.min(excelSheetProp.pageSize(),excelSheetProp.maxRowNum()); // 分页数量不能超过最大行数

                while (true) {
                    // 查询数据
                    // 设置分页参数
                    if (pageNumberField != null){
                        pageNumberField.set(param.getParams(),pageNumber++);
                    }
                    if (pageSizeField != null){
                        pageSizeField.set(param.getParams(),pageSize);
                    }

                    Collection<?> result = null;
                    try {
                        result = (Collection<?>) sheetsMethod.invoke(this,param);

                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }

                    // 写入数据
                    if (excelSheetProp.writeType() == POICodeConstants.EXCEL_WRITE_TYPE_WRITE) {
                        excelWriter.write(result,writeSheet); // 写入数据
                    } else if (excelSheetProp.writeType() == POICodeConstants.EXCEL_WRITE_TYPE_FILL) {
                        excelWriter.fill(result,writeSheet); // 填充数据
                    }

                    // 如果数据不满一页，则退出
                    if (result == null || result.size() == 0 || result.size() < pageSize) {
                        break;
                    }

                    sheetTotalRowNum += result.size();

                    // 如果数超过最大行数，则自动切换sheet
                    if (autoSwitchSheet && sheetTotalRowNum >= maxRowNum) {
                        List<List<String>> head = writeSheet.getHead();
                        Class<?> headClazz = writeSheet.getClazz();
                        if (headClazz!=null){ // 如果有表头类，则使用表头类
                            writeSheet = EasyExcel.writerSheet(sheetIndex++, realSheetName +"("+ autoSwitchSheetIndex++ +")").head(headClazz).build();
                        }else { // 如果没有表头类，则使用动态表头
                            writeSheet = EasyExcel.writerSheet(sheetIndex++, realSheetName +"("+ autoSwitchSheetIndex++ +")").head(head).build();
                        }
                        sheetTotalRowNum = 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return newFilePath;
    }

}

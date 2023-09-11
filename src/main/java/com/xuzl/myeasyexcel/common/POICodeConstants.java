package com.xuzl.myeasyexcel.common;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName POICodeConstants.java
 * @Description TODO
 * @createTime 2023-05-16 11:11
 */
public class POICodeConstants {
    public static final Long IMT_JOB_TYPE_SYNC = 2L;
    public static final Long IMT_JOB_TYPE_ASYNC = 1L;
    /*
    任务状态 1-等待执行 2-执行中 3-执行成功 99-取消执行
     */
    public static final Long IMT_TASK_STATUS_WAIT = 1L;
    public static final Long IMT_TASK_STATUS_RUNNING = 2L;
    public static final Long IMT_TASK_STATUS_DONE = 3L;
    public static final Long IMT_TASK_STATUS_CANCEL = 4L;

    public static final Long IMT_TASK_STATUS_EXCEPTION = 99L;

    public static final Long IMT_TEMPLATE_STATUS_YES = 0L;
    public static final Long IMT_TEMPLATE_STATUS_NO = 1L;


    public static final Integer DEFAULT_START_ROW = 1;

    public static final Integer DEFAULT_BATCH_COUNT = 100;

    public static final Integer DEFAULT_CORE_SIZE = 10;

    public static final String PDF_TEMPLATE_ROOT_PATH = "/pdf/";

    public static final String EXCEL_TEMPLATE_ROOT_PATH = "/excel/";

    public static final String PDF_FILE_SUFFIX = ".pdf";

    public static final String PDF_FILE_SUFFIX_DOC = ".doc";

    public static final String PDF_FILE_SUFFIX_DOCX = ".docx";

    public static final String XLSX_FILE_SUFFIX = ".xlsx";

    public static final String POI_ROOT_PATH = "poi";

    /**
     * excel写入类型，0:写入 1:填充
     */
    public static final int EXCEL_WRITE_TYPE_WRITE = 0;

    public static final int EXCEL_WRITE_TYPE_FILL = 1;

    /**
     * 属性名
     */
    public static final String ROOT_PATH_FIELD_NAME = "rootPath";
    public static final String EXCEL_PROPERTIES_FIELD_NAME = "excelProperties";
    public static final String SHEETS_METHODS_FIELD_NAME = "sheetsMethods";
    public static final String MESSAGE_SOURCE_FIELD_NAME = "messageSource";
    public static final String PARAM_CLAZZ_FIELD_NAME = "clazz";
    public static final String PAGE_NUMBER_FIELD_NAME = "pageNumberField";
    public static final String PAGE_SIZE_FIELD_NAME = "pageSizeField";
    public static final String IMT_PROPERTY_FIELD_NAME = "imtProperty";

}

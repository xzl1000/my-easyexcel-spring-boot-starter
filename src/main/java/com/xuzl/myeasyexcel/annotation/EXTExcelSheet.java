package com.xuzl.myeasyexcel.annotation;

import com.xuzl.myeasyexcel.handler.DefaultExcelSheetHandler;
import com.xuzl.myeasyexcel.handler.EXTExcelSheetHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcelSheet.java
 * @Description TODO
 * @createTime 2023-06-06 17:50
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EXTExcelSheet {
    // sheet名称
    String sheetName() default "";

    // 表头
    Class<?> headClass() default Object.class;

    // 写入类型 0:写入 1:填充
    int writeType() default 0;

    // 分页查询时每页的数量
    int pageSize() default 2000;

    // 到达行数上限时是否自动增加sheet
    boolean autoSwitchSheet() default true;

    // 单sheet最大行数
    int maxRowNum() default 100000;

    // 自定义拦截器
    Class<? extends EXTExcelSheetHandler> handler() default DefaultExcelSheetHandler.class;

}

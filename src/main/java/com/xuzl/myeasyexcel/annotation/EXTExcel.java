package com.xuzl.myeasyexcel.annotation;


import com.xuzl.myeasyexcel.handler.DefaultExcelHandler;
import com.xuzl.myeasyexcel.handler.EXTExcelHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcel.java
 * @Description TODO
 * @createTime 2023-06-06 17:49
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EXTExcel {
    // 业务名称（唯一，默认类名）
    String value() default "";

    // 文件名称
    String fileName() default "";

    // 是否携带日期后缀
    boolean withDateSuffix() default false;

    // 时间格式
    String suffixFormat() default "yyyyMMdd";

    // 模板名称
    String templateName() default "";

    // 自定义拦截器
    Class<? extends EXTExcelHandler> handler() default DefaultExcelHandler.class;

}

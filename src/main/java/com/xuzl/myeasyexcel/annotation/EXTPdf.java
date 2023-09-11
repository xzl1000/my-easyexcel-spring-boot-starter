package com.xuzl.myeasyexcel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTPdf.java
 * @Description TODO
 * @createTime 2023-06-21 12:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EXTPdf {
    /*
     * 业务名称（唯一，默认类名）
     * @return
     */
    String value();

    /**
     * 模板文件名称
     *
     * @return
     */
    String templateName() default "";

    /**
     * 输出文件名称
     *
     * @return
     */
    String fileName() default "";

    /**
     * 模板类型,默认 pdf
     *
     * @return {@link String}
     */
    String templateType() default ".pdf";

    /**
     * 是否携带日期后缀
     */
    boolean withDateSuffix() default false;

    /**
     * 时间格式
     */
    String suffixFormat() default "yyyyMMdd";

}

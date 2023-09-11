package com.xuzl.myeasyexcel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTExcel.java
 * @Description TODO
 * @createTime 2023-07-18 14:43
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IMTExcel {
    // 业务名称（唯一，默认类名）
    String value() default "";

    /**
     * 单次循环行数
     */
    int loopRowNumber() default 1000;

    /**
     * 开始行数
     */
    int startRowNumber() default 1;

    /**
     * 1-异步，2-同步
     */
    int type() default 2;

    /**
     * 任务最大线程数
     */
    int maxActiveThread() default 10;
}

package com.xuzl.myeasyexcel.register;

import com.xuzl.myeasyexcel.annotation.IMTExcel;
import com.xuzl.myeasyexcel.common.ImtExcelProperties;
import com.xuzl.myeasyexcel.common.NamedThreadFactory;
import com.xuzl.myeasyexcel.common.POICodeConstants;
import com.xuzl.myeasyexcel.config.ExecutorProperties;
import com.xuzl.myeasyexcel.executor.IMTBaseExecutor;
import com.xuzl.myeasyexcel.utils.ReflectUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName DefaultImtExcelRegister.java
 * @Description TODO
 * @createTime 2023-10-12 11:43
 */
public class DefaultImtExcelRegister implements ImtExcelRegister{

    private final ApplicationContext applicationContext;

    private final MessageSource messageSource;

//    Log log = Log.getLogger(DefaultImtExcelRegister.class);

    public DefaultImtExcelRegister(ApplicationContext applicationContext, MessageSource messageSource) {
        this.applicationContext = applicationContext;
        this.messageSource = messageSource;
    }

    @Override
    public HashMap<Object, IMTBaseExecutor<?, ?>> register(ExecutorProperties executorProperties) {
        HashMap<Object, IMTBaseExecutor<?, ?>> executorMap = new HashMap<>();
        String scanPackage = executorProperties.getScanPackage();
        if (scanPackage == null || scanPackage.length() == 0) {
            throw new RuntimeException("scanPackage can not be null!");
        }
        // 扫描路径下的所有类
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(IMTExcel.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(scanPackage)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                IMTExcel imtExcel = clazz.getAnnotation(IMTExcel.class);
                if (imtExcel == null) {
                    continue;
                }
                String executorName = imtExcel.value();
                if (executorName==null || executorName.trim().equals("")) {
                    throw new RuntimeException("executorName can not be null!");
                }
                if (executorMap.containsKey(executorName)) {
                    throw new RuntimeException("executorName: " + executorName + " is already exist!");
                }

                // 从容器中获取bean
                IMTBaseExecutor<?, ?> executor = (IMTBaseExecutor<?, ?>) applicationContext.getBean(clazz);

                // 反射设置父类私有属性
                Class<?> baseClass = IMTBaseExecutor.class;
                Field imtProperty = baseClass.getDeclaredField(POICodeConstants.IMT_PROPERTY_FIELD_NAME);
                Field messageSource = baseClass.getDeclaredField(POICodeConstants.MESSAGE_SOURCE_FIELD_NAME);
                Field clazzField = baseClass.getDeclaredField(POICodeConstants.PARAM_CLAZZ_FIELD_NAME);
                Field rClazzField = baseClass.getDeclaredField("rClazz");
                Field threadPoolExecutorField = baseClass.getDeclaredField(POICodeConstants.THREAD_POOL);

                ImtExcelProperties properties = new ImtExcelProperties();
                properties.setExecutorName(executorName);
                properties.setType(imtExcel.type());
                properties.setStartRowNumber(imtExcel.startRowNumber());
                properties.setMaxActiveThread(imtExcel.maxActiveThread());

                imtProperty.setAccessible(true);
                imtProperty.set(executor, properties);

                messageSource.setAccessible(true);
                messageSource.set(executor, this.messageSource);

                // 设置clazz为泛型T的类型
                clazzField.setAccessible(true);
                clazzField.set(executor, ReflectUtils.getGenericClass(executor.getClass()));

                rClazzField.setAccessible(true);
                rClazzField.set(executor, ReflectUtils.getGenericClass(executor.getClass(), 1));

                int coreSize = imtExcel.maxActiveThread();

                // 创建线程池
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                        coreSize, // 核心线程数
                        coreSize, // 最大线程数
                        30, // 空闲线程存活时间
                        TimeUnit.SECONDS, // 时间单位
                        new LinkedBlockingQueue<>(),
                        new NamedThreadFactory("imt-"+imtExcel.value()));

                // 设置属性
                threadPoolExecutorField.setAccessible(true);
                threadPoolExecutorField.set(executor,threadPoolExecutor);

                executorMap.put(executorName, executor);
            } catch (Exception e) {
                e.printStackTrace();
//                log.error("registerImtExecutor error,beanDefinition:{}", beanDefinition, e);
            }
        }

//        log.info("--------------------------init imt executor success,executorMap:{}", Arrays.toString(executorMap.keySet().toArray()));
        return executorMap;
    }
}

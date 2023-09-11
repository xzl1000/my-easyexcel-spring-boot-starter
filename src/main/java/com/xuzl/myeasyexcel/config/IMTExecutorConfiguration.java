package com.xuzl.myeasyexcel.config;

import com.xuzl.myeasyexcel.annotation.IMTExcel;
import com.xuzl.myeasyexcel.common.POICodeConstants;
import com.xuzl.myeasyexcel.executor.IMTBaseExecutor;
import com.xuzl.myeasyexcel.router.IMTRouter;
import com.xuzl.myeasyexcel.utils.ReflectUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTExecutorConfiguration.java
 * @Description TODO
 * @createTime 2023-05-15 18:21
 */
@Configuration
@EnableConfigurationProperties(IMTExecutorProperties.class)
public class IMTExecutorConfiguration {

//    Log log = Log.getLogger(IMTExecutorConfiguration.class);

    IMTExecutorProperties imtExecutorProperties;

    private final ApplicationContext applicationContext;

    private final MessageSource messageSource;

    public IMTExecutorConfiguration(IMTExecutorProperties imtExecutorProperties, ApplicationContext applicationContext, MessageSource messageSource) {
        this.imtExecutorProperties = imtExecutorProperties;
        this.applicationContext = applicationContext;
        this.messageSource = messageSource;
    }


    @Bean
    @ConditionalOnProperty(prefix = "poi.excel.imt", name = "enable", havingValue = "true")
    public IMTRouter registerImtExecutor() {
        Map<String, IMTBaseExecutor<?>> executorMap = new HashMap<>();
        String scanPackage = imtExecutorProperties.getScanPackage();
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
                IMTBaseExecutor<?> executor = (IMTBaseExecutor<?>) applicationContext.getBean(clazz);

                // 反射设置父类私有属性
                Class<?> superclass = clazz.getSuperclass();
                Field imtProperty = superclass.getDeclaredField(POICodeConstants.IMT_PROPERTY_FIELD_NAME);
                Field messageSource = superclass.getDeclaredField(POICodeConstants.MESSAGE_SOURCE_FIELD_NAME);
                Field clazzField = superclass.getDeclaredField(POICodeConstants.PARAM_CLAZZ_FIELD_NAME);

                imtProperty.setAccessible(true);
                imtProperty.set(executor, imtExcel);

                messageSource.setAccessible(true);
                messageSource.set(executor, this.messageSource);

                // 设置clazz为泛型T的类型
                clazzField.setAccessible(true);
                clazzField.set(executor, ReflectUtils.getGenericClass(executor.getClass()));

                executorMap.put(executorName, executor);
            } catch (Exception e) {
                e.printStackTrace();
//                log.error("registerImtExecutor error,beanDefinition:{}", beanDefinition, e);
            }
        }

//        log.info("--------------------------init imt executor success,executorMap:{}", Arrays.toString(executorMap.keySet().toArray()));

        return new IMTRouter(executorMap);

    }

}

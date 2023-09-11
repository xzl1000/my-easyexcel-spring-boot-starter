package com.xuzl.myeasyexcel.config;

import com.xuzl.myeasyexcel.annotation.EXTExcel;
import com.xuzl.myeasyexcel.annotation.EXTExcelSheet;
import com.xuzl.myeasyexcel.common.EXTExecuteParam;
import com.xuzl.myeasyexcel.common.POICodeConstants;
import com.xuzl.myeasyexcel.executor.EXTExcelBaseExecutor;
import com.xuzl.myeasyexcel.router.EXTExcelRouter;
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
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcelExecutorConfiguration.java
 * @Description TODO
 * @createTime 2023-06-28 15:35
 */
@Configuration
@EnableConfigurationProperties(EXTExcelExecutorProperties.class)
public class EXTExcelExecutorConfiguration {

    EXTExcelExecutorProperties extExcelExecutorProperties;

    private final ApplicationContext applicationContext;

    private final MessageSource messageSource;

    public EXTExcelExecutorConfiguration(EXTExcelExecutorProperties extExcelExecutorProperties, ApplicationContext applicationContext, MessageSource messageSource) {
        this.extExcelExecutorProperties = extExcelExecutorProperties;
        this.applicationContext = applicationContext;
        this.messageSource = messageSource;
    }

    @Bean
    @ConditionalOnProperty(prefix = "poi.excel.ext", name = "enable", havingValue = "true")
    public EXTExcelRouter registerExtExcelExecutor() {
        String scanPackage = extExcelExecutorProperties.getScanPackage();
        if (scanPackage == null || scanPackage.length() == 0) {
            throw new RuntimeException("scanPackage can not be null!");
        }
        Map<String, EXTExcelBaseExecutor<?>> executorMap = new HashMap<>();
        // 扫描路径下的所有类
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EXTExcel.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(scanPackage)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                EXTExcel annotation = clazz.getAnnotation(EXTExcel.class);
                if (annotation != null) {
                    String executorName = annotation.value();
                    if (executorName==null || executorName.trim().equals("")) {
                        throw new RuntimeException("executorName can not be null!");
                    }
                    // 保证唯一性
                    if (executorMap.containsKey(executorName)) {
                        throw new RuntimeException("executorName " + executorName + " is already exist!");
                    }
                    // 扫描类下所有被@EXTExcelSheet注解的方法
                    List<Method> sheetMethods = new ArrayList<>();

                    Method[] declaredMethods = clazz.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        if (method.isAnnotationPresent(EXTExcelSheet.class)) {
                            // 检查方法形参和返回值类型
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            if (parameterTypes.length != 1 || !parameterTypes[0].isAssignableFrom(EXTExecuteParam.class)) {
                                throw new RuntimeException("method " + method.getName() + " must be com.shuabao.poi.core.common.EXTExecuteParam<?>!");
                            }

                            Class<?> returnType = method.getReturnType();
                            if (!Collection.class.isAssignableFrom(returnType)) {
                                throw new RuntimeException("method " + method.getName() + " must return java.util.Collection<?>!");
                            }
                            sheetMethods.add(method);
                        }
                    }

                    if (sheetMethods.size() == 0) {
                        throw new RuntimeException("class " + clazz.getName() + " must have at least one method annotated by @EXTExcelSheet!");
                    }

                    // 按方法名称排序
                    sheetMethods.sort(Comparator.comparing(Method::getName));

                    // 从容器中获取bean
                    EXTExcelBaseExecutor<?> executor = (EXTExcelBaseExecutor<?>) applicationContext.getBean(clazz);

                    // 反射设置父类私有属性
                    Class<?> superclass = clazz.getSuperclass();
                    Field rootPath = superclass.getDeclaredField(POICodeConstants.ROOT_PATH_FIELD_NAME);
                    Field excelProperties = superclass.getDeclaredField(POICodeConstants.EXCEL_PROPERTIES_FIELD_NAME);
                    Field sheetsMethods = superclass.getDeclaredField(POICodeConstants.SHEETS_METHODS_FIELD_NAME);
                    Field messageSource = superclass.getDeclaredField(POICodeConstants.MESSAGE_SOURCE_FIELD_NAME);
                    Field paramClazzField = superclass.getDeclaredField(POICodeConstants.PARAM_CLAZZ_FIELD_NAME);
                    Field pageNumberField = superclass.getDeclaredField(POICodeConstants.PAGE_NUMBER_FIELD_NAME);
                    Field pageSizeField = superclass.getDeclaredField(POICodeConstants.PAGE_SIZE_FIELD_NAME);

                    messageSource.setAccessible(true);
                    messageSource.set(executor, this.messageSource);

                    rootPath.setAccessible(true);
                    rootPath.set(executor, extExcelExecutorProperties.getRootPath());

                    excelProperties.setAccessible(true);
                    excelProperties.set(executor,annotation);

                    sheetsMethods.setAccessible(true);
                    sheetsMethods.set(executor,sheetMethods);

                    paramClazzField.setAccessible(true);
                    Class<?> paramClass = ReflectUtils.getGenericClass(clazz);
                    paramClazzField.set(executor, paramClass);

                    Field pageNumber = ReflectUtils.getField(paramClass, "pageNumber");
                    if (pageNumber != null) {
                        pageNumberField.setAccessible(true);
                        pageNumber.setAccessible(true);
                        pageNumberField.set(executor, pageNumber);
                    }

                    Field pageSize = ReflectUtils.getField(paramClass, "pageSize");
                    if (pageSize != null) {
                        pageSizeField.setAccessible(true);
                        pageSize.setAccessible(true);
                        pageSizeField.set(executor, pageSize);
                    }

                    executorMap.put(executorName, executor);
                }
            } catch (Exception e) {
//                log.error("registerExtExcelExecutor error, executor class name: {}", beanDefinition.getBeanClassName());
                e.printStackTrace();
            }
        }
//        log.info("--------------------------init ext excel executor success,executorMap:{}", Arrays.toString(executorMap.keySet().toArray()));
        return new EXTExcelRouter(executorMap);
    }
}

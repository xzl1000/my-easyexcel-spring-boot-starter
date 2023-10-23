package com.xuzl.myeasyexcel.config;

import com.xuzl.myeasyexcel.register.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ExecutorAutoConfiguration.java
 * @Description TODO
 * @createTime 2023-10-12 10:22
 */
@Configuration
@EnableConfigurationProperties(ExecutorProperties.class)
public class ExecutorAutoConfiguration {
    private final ExecutorProperties executorProperties;
    @Resource
    private ExtExcelRegister extExcelRegister;
    @Resource
    private ImtExcelRegister imtExcelRegister;

    private final ApplicationContext applicationContext;

    private final MessageSource messageSource;

    public ExecutorAutoConfiguration(ExecutorProperties executorProperties, ApplicationContext applicationContext, MessageSource messageSource) {
        this.executorProperties = executorProperties;
        this.applicationContext = applicationContext;
        this.messageSource = messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(ExtExcelRegister.class)
    public ExtExcelRegister defaultExtExcelRegister() {
        return new DefaultExtExcelRegister(applicationContext, messageSource);
    }

    @Bean
    @ConditionalOnMissingBean(ImtExcelRegister.class)
    public ImtExcelRegister defaultImtExcelRegister() {
        return new DefaultImtExcelRegister(applicationContext, messageSource);
    }

    @Bean
    public ExecutorGlobalContext executorGlobalContext() {
        return new ExecutorGlobalContext(
                extExcelRegister.register(executorProperties),
                imtExcelRegister.register(executorProperties)
        );
    }
}

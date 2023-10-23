package com.xuzl.myeasyexcel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ExecutorProperties.java
 * @Description TODO
 * @createTime 2023-10-12 10:19
 */
@ConfigurationProperties(prefix = "myeasyexcel")
public class ExecutorProperties {
    private String scanPackage;

    private String rootPath;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}

package com.xuzl.myeasyexcel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName EXTExcelExecutorProperties.java
 * @Description TODO
 * @createTime 2023-06-28 15:33
 */

@ConfigurationProperties(prefix = "poi.excel.ext")
public class EXTExcelExecutorProperties {
    private boolean enable = false;

    private String scanPackage;

    private String rootPath;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

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

package com.xuzl.myeasyexcel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName IMTExecutorProperties.java
 * @Description TODO
 * @createTime 2023-05-15 18:27
 */
@ConfigurationProperties(prefix = "poi.excel.imt")
public class IMTExecutorProperties {
    private boolean enable;

    private String scanPackage;

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}

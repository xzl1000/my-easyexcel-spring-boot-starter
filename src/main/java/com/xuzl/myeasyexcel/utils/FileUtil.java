package com.xuzl.myeasyexcel.utils;


import com.xuzl.myeasyexcel.common.POICodeConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName FileUtil.java
 * @Description TODO
 * @createTime 2023-06-25 17:29
 */
public class FileUtil {

    private static final SimpleDateFormat sdf = new SimpleDateFormat(POICodeConstants.DEFAULT_DATE_FORMAT);

    private static String getSystemDate() {
        return sdf.format(System.currentTimeMillis());
    }

    public static String createNewPdfFile(String rootPath,String filename){

        if (rootPath.endsWith(File.separator)) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }

        rootPath = rootPath
                + File.separator + POICodeConstants.POI_ROOT_PATH
                + File.separator + getSystemDate()
                + File.separator + UUID.randomUUID().toString().replace("-","");
        filename = filename + POICodeConstants.PDF_FILE_SUFFIX;

        try {
            Files.createDirectories(new File(rootPath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rootPath + File.separator + filename;
    }

    /**
     * 创建文件内  /rootPath/fileName.fileType
     *
     * @param rootPath 根路径
     * @param fileName 文件名称
     * @param fileType 文件类型 .pdf or .doc or .docx
     * @return {@link String}
     */
    public static String createNewFile(String rootPath, String fileName, String fileType) {
        if (rootPath.endsWith(File.separator)) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
        rootPath = rootPath
                + File.separator + POICodeConstants.POI_ROOT_PATH
                + File.separator + getSystemDate()
                + File.separator + UUID.randomUUID().toString().replace("-", "");
        fileName = fileName + fileType;

        try {
            Files.createDirectories(new File(rootPath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rootPath + File.separator + fileName;
    }

    public static String createNewXlsxFile(String rootPath,String filename, Integer type){

        if (rootPath.endsWith(File.separator)) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }

        rootPath = rootPath
                + File.separator + POICodeConstants.POI_ROOT_PATH
                + File.separator + (type==POICodeConstants.IMT_JOB_TYPE_ASYNC.intValue()?"async":"sync")
                + File.separator + getSystemDate()
                + File.separator + UUID.randomUUID().toString().replace("-","");
        filename = filename + POICodeConstants.XLSX_FILE_SUFFIX;

        try {
            Files.createDirectories(new File(rootPath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rootPath + File.separator + filename;
    }
}

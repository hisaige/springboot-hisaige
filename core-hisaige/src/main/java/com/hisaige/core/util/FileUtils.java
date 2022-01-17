package com.hisaige.core.util;

/**
 * @author chenyj
 * 2019/10/24 - 13:14.
 **/
public class FileUtils extends org.apache.commons.io.FileUtils {
    private static final String RESOURCE_PATH = FileUtils.class.getResource("/").getPath().substring(1);

    /**
     * 获取resource资源路径
     * @return resource资源路径
     */
    public static String getResourcePath(){
        return RESOURCE_PATH;
    }

}

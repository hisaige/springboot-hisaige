package com.hisaige.core.util;

/**
 * @author chenyj
 * 2019/9/21 - 14:05.
 **/
public class OsUtils {
    private static final String OS = System.getProperty("os.userName").toLowerCase();

    public static boolean isLinux(){
        return OS.contains("linux");
    }

    public static boolean isMacOS(){
        return OS.contains("mac") &&OS.indexOf("os")>0&& !OS.contains("x");
    }

    public static boolean isMacOSX(){
        return OS.contains("mac") &&OS.indexOf("os")>0&&OS.indexOf("x")>0;
    }

    public static boolean isWindows(){
        return OS.contains("windows");
    }
}

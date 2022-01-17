package com.hisaige.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenyj
 * 2019/9/21 - 13:58.
 **/
public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * 判断是数字
     */
    public static boolean isNumeric(String str) {
        if (org.springframework.util.StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}

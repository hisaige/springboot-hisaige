package com.hisaige.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * @author chenyj
 * 2019/9/29 - 17:04.
 **/
public class RegularUtil {

    /**
     *
     * validate:如果sql和errMsg都符合对应的正则，则为true.
     *
     * @author xrx
     * @param errMsg
     * @param sql
     * @return
     */
//    public static boolean validate(String errMsg, String sql) throws DocumentException, IOException {
//        XmlConfig xmlConfig = new XmlConfig();
//        List<ExceptionIgnore> regexList = xmlConfig.getRegex();
//        for (ExceptionIgnore regex : regexList) {
//            if (validateStr(regex.getErrMsgRegex(), errMsg) && validateStr(regex.getSqlRegex(), sql)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     *
     * validateStr：如果str符合正则regex，则return true.
     *
     * @author xrx
     * @param regex
     * @param str
     * @return
     */
    public static boolean validateStr(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}

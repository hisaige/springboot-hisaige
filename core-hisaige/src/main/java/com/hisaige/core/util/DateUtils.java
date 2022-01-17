package com.hisaige.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author chenyj
 * 2019/9/21 - 14:00.
 **/
public class DateUtils {


    /**
     * 获取当前系统日期和时间
     *
     * @return 当前系统日期和时间时间格式"2004-08-23 08:34:35"
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 新建文件的时候，往往需要日期作为文件名，这里定义一个统一格式
     * @return String
     */
    public static String getformatDate() {
        return getCurrentDateTime("yyyy-MM-dd");
    }

    /**
     * 获取当前系统日期和时间
     *
     * @param format
     *            显示格式
     * @return String 格式后的时间字符串
     */
    public static String getCurrentDateTime(String format) {
        return formatDate(new Date(), format);
    }

    /**
     * 格式化date
     *
     * @param dt
     *            Date
     * @param format
     *            String
     * @return String
     */
    public static String formatDate(Date dt, String format) {
        if (dt == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            return formatCalendar(cal, format);
        }
    }

    /**
     * 格式化时间的显示
     *
     * @param cal
     *            Calendar Object
     * @param format
     *            日期格式化的标准 e.g. "yyyy/MM/dd
     *            HH:mm:ss"（务必按标准写，可参考java.text.simpleDateFormat.java)
     * @return String 格式化的时间
     */
    public static String formatCalendar(Calendar cal, String format) {
        if (cal == null) {
            return null;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(cal.getTime());
        }
    }
}

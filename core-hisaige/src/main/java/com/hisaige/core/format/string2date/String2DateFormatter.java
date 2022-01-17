package com.hisaige.core.format.string2date;

import com.hisaige.core.util.StringUtils;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author chenyj
 * 2019/12/8 - 23:18.
 **/
public class String2DateFormatter implements Formatter<Date> {

    //锁
    private static final Object lockObj = new Object();

    @Nullable
    private final String pattern;

    @Nullable
    private TimeZone timeZone;

    //存放不同日期模板格式的map缓存
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    public static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    public String2DateFormatter(String pattern) {
        this.pattern = pattern;
    }


    /**
     * 设置时区
     *
     * @param timeZone 时区
     */
    protected void setTimeZone(@Nullable TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * 前端传值给后端
     * @param text 前端传递的时间戳
     * @param locale 时间locale
     * @return Date 转换后的时间
     * @throws ParseException 异常则抛出
     */
    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        if (!StringUtils.isEmpty(pattern)) {
            SimpleDateFormat simpleDateFormat = getSdf(pattern);
            if (null != timeZone) {
                simpleDateFormat.setTimeZone(timeZone);
            }
            return simpleDateFormat.parse(text);
        } else {
            long time = Long.parseLong(text);
            return new Date(time);
        }
    }

    @Override
    public String print(Date date, Locale locale) {

        if (!StringUtils.isEmpty(pattern)) {
            SimpleDateFormat simpleDateFormat = getSdf(pattern);
            if (null != timeZone) {
                simpleDateFormat.setTimeZone(timeZone);
            }
            return simpleDateFormat.format(date);
        } else {
            return String.valueOf(date.getTime());
        }
    }


    //    protected DateFormat getDateFormat(Locale locale) {
//        DateFormat dateFormat = new DateFO(locale);
//        if (this.timeZone != null) {
//            dateFormat.setTimeZone(this.timeZone);
//        }
//        dateFormat.setLenient(this.lenient);
//        return dateFormat;
//    }

}

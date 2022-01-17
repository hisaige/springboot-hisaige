package com.hisaige.core.json.fastJson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 自定义序列化
 * @author chenyj
 * 2019/12/14 - 23:36.
 **/
public class UtcDateTimeSerializer implements ObjectSerializer {

    //日期 按照 utc 时区进行序列化
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("UTC");
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {

        Date value = (Date) object;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN);
        simpleDateFormat.setTimeZone(TIMEZONE);
        //当前字段序列化值
        serializer.write(simpleDateFormat.format(value));
    }
}

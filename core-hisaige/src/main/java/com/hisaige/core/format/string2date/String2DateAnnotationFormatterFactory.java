package com.hisaige.core.format.string2date;

import com.hisaige.core.annotation.String2DateFormat;
import com.hisaige.core.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author chenyj
 * 2019/12/10 - 22:52.
 **/
public class String2DateAnnotationFormatterFactory extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<String2DateFormat> {

    private static final Set<Class<?>> FIELD_TYPES;

    static {
        Set<Class<?>> fieldTypes = new HashSet<>(2);
        fieldTypes.add(String.class);
        fieldTypes.add(Date.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }

    @NotNull
    @Override
    public Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    @NotNull
    @Override
    public Printer<?> getPrinter(String2DateFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    @NotNull
    @Override
    public Parser<?> getParser(String2DateFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    @NotNull
    private Formatter<Date> getFormatter(String2DateFormat annotation) {
        String2DateFormatter string2DateFormatter = new String2DateFormatter(annotation.pattern());
        String timeZoneStr = annotation.timeZone();
        if(!StringUtils.isEmpty(timeZoneStr)){
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            string2DateFormatter.setTimeZone(timeZone);
        }
        return string2DateFormatter;
    }

}

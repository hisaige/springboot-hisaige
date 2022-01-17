package com.hisaige.core.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.hisaige.core.format.string2date.String2DateAnnotationFormatterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyj
 * 2019/9/21 - 19:56.
 **/
@Configuration
public class CoreMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");
    }

    /**
     * FastJsonHttpMessageConverter转换器
     * 注意  如果使用了当前转换器,则@JsonFormat,@DateTimeFormat这两个注解  对 后台 --> 前端  的数据作用失效
     * 可以使用 如 @JSONField(format ="yyyy-MM-dd HH:mm:ss") 代替,注意 这里的时区和系统设置的时区一样
     * @param converters fastJson转换器
     */
    @Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = new FastJsonConfig();

		// 自定义格式化输出,因为map序列化的结果,和序列化对象类似,会把map中的所有key看做对象的属性差不多
		//因此不对map做特殊处理,为null就不序列化
		fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullStringAsEmpty,SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.PrettyFormat);

		// 中文乱码解决方案
		List<MediaType> mediaTypes = new ArrayList<>();
		// 设定json格式且编码为UTF-8
		mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		converter.setSupportedMediaTypes(mediaTypes);
		converter.setFastJsonConfig(fastJsonConfig);
		//添加到第一位置，这样在遍历使用时可以得到优先使用权限
		converters.add(0, converter);
	}

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldAnnotation(new String2DateAnnotationFormatterFactory());
    }
}

package com.hisaige.core.config;

import com.alibaba.fastjson.JSON;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass({JSON.class})
public class HttpMessageConfiguration implements WebMvcConfigurer {



	/**
	 * 以下这种方式也行
	 */
//	@Bean
//	@ConditionalOnMissingBean
//	public HttpMessageConverters fastjsonConverter() {
//		FastJsonConfig fastJsonConfig = new FastJsonConfig();
//		// 自定义格式化输出
//		fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullListAsEmpty,
//				SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty,
//				SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.PrettyFormat);
//
//		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
//		// 中文乱码解决方案
//		List<MediaType> mediaTypes = new ArrayList<>();
//		mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);// 设定json格式且编码为UTF-8
//		fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
//
//		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
//
//		return new HttpMessageConverters(fastJsonHttpMessageConverter);
//	}
}

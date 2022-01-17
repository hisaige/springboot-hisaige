package com.hisaige.core.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.hisaige.core.config.properties.RestTemplateProperty;
import com.hisaige.core.config.properties.SystemProperties;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
/**
 * RestTemplate配置类，用于发送http请求
 *
 * @author chenyj
 * 2019年10月19日
 */
@Configuration
@ConditionalOnClass(value = { RestTemplate.class, HttpClient.class})
@EnableConfigurationProperties({RestTemplateProperty.class})
@AllArgsConstructor
public class RestTemplateConfiguration {

	private RestTemplateProperty httpClientProperty;
	
	private SystemProperties commonSystemProperty;

	// 创建HTTP客户端工厂 @Deprecated 分模块代替
	/*
	 * private ClientHttpRequestFactory createFactory() { if
	 * (httpConfObj.getMaxTotalConnect() <= 0) { SimpleClientHttpRequestFactory
	 * factory = new SimpleClientHttpRequestFactory();
	 * factory.setConnectTimeout(httpConfObj.getConnectTimeout());
	 * factory.setReadTimeout(httpConfObj.getReadTimeout()); return factory; }
	 * HttpClient httpClient =
	 * HttpClientBuilder.create().setMaxConnTotal(httpConfObj.getMaxTotalConnect())
	 * .setMaxConnPerRoute(httpConfObj.getMaxConnectPerRoute()).build();
	 * HttpComponentsClientHttpRequestFactory factory = new
	 * HttpComponentsClientHttpRequestFactory(httpClient);
	 * factory.setConnectTimeout(httpConfObj.getConnectTimeout());
	 * factory.setReadTimeout(httpConfObj.getReadTimeout()); return factory; }
	 */

	// 初始化RestTemplate,并加入spring的Bean工厂，由spring统一管理
	@Bean
	@ConditionalOnMissingBean(RestTemplate.class)
	public RestTemplate restTemplate(RestTemplateBuilder builder) {

		ClientHttpRequestFactory clientHttpRequestFactory = clientHttpRequestFactory();
		Supplier<ClientHttpRequestFactory> supplier = () -> clientHttpRequestFactory;
		RestTemplate restTemplate = builder.requestFactory(supplier).build();
//		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		// 重新设置StringHttpMessageConverter字符集为UTF-8，解决中文乱码问题
		HttpMessageConverter<?> converterTarget = null;
		for (HttpMessageConverter<?> item : converterList) {
			if (StringHttpMessageConverter.class == item.getClass()) {
				converterTarget = item;
				break;
			}
		}
		if (null != converterTarget) {
			converterList.remove(converterTarget);
		}
		converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		/*
		 * 加入FastJson转换器 根据使用情况进行操作，此段注释，默认使用jackson
		 * spring的json转换器默认使用的是Jackson，json字符串和对应的Entity如果有字段对不上就会报错，这点很重要(踩过大坑。。。)
		 * 如果使用的实体信息比较标准，可以使用jackson，个人使用FastJsonHttpMessageConverter，可以将请求结果直接转成JSONObject
		 */
		converterList.add(new FastJsonHttpMessageConverter());
		return restTemplate;
	}

	// FastJSON替换Jackson配置,当然不配置也能输出,配置本转换器后，接口返回的null值会转化为空字符串，默认不配置
	//千万不要在这里配置统一HttpMessageConverters转换器，否则@JsonFormat，@datetimeformat 对前端的参数返回值将不起作用,
	// 在CoreMvcConfiguration中配置即可
//	@Bean
//	@ConditionalOnMissingBean
//	public HttpMessageConverters fastJsonConverter() {
//		FastJsonConfig fastJsonConfig = new FastJsonConfig();
//		// 自定义格式化输出
////		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty,
////				SerializerFeature.WriteNullNumberAsZero); // 会将null值写成空字符串
//		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.WriteNullNumberAsZero);
//
//		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
//		// 中文乱码解决方案
//        List<MediaType> mediaTypes = new ArrayList<>();
//        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);//设定json格式且编码为UTF-8
//        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
//
//        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
//
//		return new HttpMessageConverters(fastJsonHttpMessageConverter);
//	}

	@Bean
	@ConditionalOnMissingBean
	public HttpClientConnectionManager poolingConnectionManager() {
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
		poolingConnectionManager.setMaxTotal(httpClientProperty.getMaxTotalConnect()); // 连接池最大连接数
		poolingConnectionManager.setDefaultMaxPerRoute(httpClientProperty.getMaxConnectPerRoute()); // 每个主机的并发
		return poolingConnectionManager;
	}

	@Bean
	@ConditionalOnMissingBean
	public HttpClientBuilder httpClientBuilder() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// 设置HTTP连接管理器
		httpClientBuilder.setConnectionManager(poolingConnectionManager());
		httpClientBuilder
				.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientProperty.getRequestRetry(), true));
		return httpClientBuilder;
	}

	@Bean
	@ConditionalOnMissingBean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		if (httpClientProperty.getMaxTotalConnect() <= 0) {
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			factory.setConnectTimeout(httpClientProperty.getConnectTimeout());
			factory.setReadTimeout(httpClientProperty.getReadTimeout());
			return factory;
		} else {
			HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
			clientHttpRequestFactory.setHttpClient(httpClientBuilder().build());
			clientHttpRequestFactory.setConnectTimeout(httpClientProperty.getConnectTimeout()); // 连接超时，毫秒
			clientHttpRequestFactory.setReadTimeout(httpClientProperty.getReadTimeout()); // 读写超时，毫秒
			clientHttpRequestFactory.setConnectionRequestTimeout(httpClientProperty.getConnectionRequestTimeout()); // 读写超时，毫秒
			if (commonSystemProperty.getEnableProxy()) { //设置代理，用于抓包
				CloseableHttpClient client = HttpClientBuilder.create()
                .setProxy(new HttpHost(commonSystemProperty.getProxyHost(), Integer.valueOf(commonSystemProperty.getProxyPort())))
                .build();
				clientHttpRequestFactory.setHttpClient(client);
			}
			return clientHttpRequestFactory;
		}
	}
}

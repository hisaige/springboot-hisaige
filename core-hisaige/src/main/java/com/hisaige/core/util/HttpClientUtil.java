package com.hisaige.core.util;

import com.google.common.collect.Lists;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @deprecated 没啥卵用  用HttpOperation代替
 * http请求封装
 * 使用方式：
 * 	try (HttpClientUtil httpClientUtil = new HttpClientUtil();) {
			System.out.println(httpClientUtil.doGet("https://www.baidu.com/", null, null));
		}
 * @author chenyj
 */

public class HttpClientUtil implements Closeable {

	private CloseableHttpClient httpClient = null;

	/**
	 * 创建不使用代理的httpclient，不没有代理一般不能用抓包工具抓包
	 */
	public HttpClientUtil() {
//		this.httpClient = HttpClientBuilder.create().build();
		if (this.httpClient == null) {
			this.httpClient = HttpClients.createDefault();
		}
	}

	/**
	 * 建立存在代理的HttpClient对象 利用抓包工具代理端口可以抓取http包，如ip=127.0.0.1 port=8888,可以使用fiddler抓包
	 * 
	 * @param proxyIP 代理IP地址
	 * @param port    代理端口号
	 */
	public HttpClientUtil(String proxyIP, int port) {

		HttpHost proxy = new HttpHost(proxyIP, port);
		RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(20000).build();
		this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	/**
	 * 使用完毕应关闭httpclient
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		if (this.httpClient != null) {
			httpClient.close();
		}
	}

	/**
	 * 一般的POST请求
	 * 
	 * @param uri
	 * @param paramsMap
	 * @param headereMap
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String doPost(String uri, Map<String, Object> paramsMap, Map<String, String> headerMap)
			throws ClientProtocolException, IOException {

		HttpPost httpPost = new HttpPost(uri);// 建立Post对象

		Header[] headers = buildHeader(headerMap);// 设置请求�?
		httpPost.setHeaders(headers);

		StringEntity entity = buildEnntity(paramsMap);// 请求实体
		httpPost.setEntity(entity);
		return getResponse(httpPost);

	}

	/**
	 * 以raw形式发送post请求，如果字符串参数不是json而是数组，则paramsMap的键设置为字符串“null”,值为字符串参数
	 * @param uri
	 * @param entityStr
	 * @param headerMap
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String doRawPost(String uri, String entityStr, Map<String, String> headerMap)
			throws ClientProtocolException, IOException {

		HttpPost httpPost = new HttpPost(uri);// 建立Post对象

		Header[] headers = buildHeader(headerMap);// 设置请求�?
		httpPost.setHeaders(headers);

		StringEntity entity = null;// = buildRawEnntity(paramsMap);//请求实体
		entity = new StringEntity(entityStr);
		entity.setContentType("application/json; charset=utf-8");
		httpPost.setEntity(entity);

		return getResponse(httpPost);
	}

	/**
	 * 一般的Get请求
	 * 
	 * @param uri
	 * @param paramsMap  请求参数
	 * @param headereMap 请求头参�?
	 * @return JSONObject
	 * @throws IOException
	 * @throws ParseException
	 */
	public String doGet(String uri, Map<String, String> paramsMap, Map<String, String> headereMap)
			throws ParseException, IOException {

		uri += "?";
		List<NameValuePair> params = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(paramsMap)) {
			for (Entry<String, String> entry : paramsMap.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		HttpResponse response;
		HttpGet httpGet = null;
		try {
			HttpClientContext context = HttpClientContext.create();// 创建HttpClient上下�?
			uri += EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));// 构建请求�?
			httpGet = new HttpGet(uri);// 建立Get对象

			Header[] headers = buildHeader(headereMap);// 设置请求体
			httpGet.setHeaders(headers);
			response = httpClient.execute(httpGet, context);

			if (response.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
//				JSONObject jsonObj = JSONObject.parseObject(content);
//				if (uri.contains("login")) { 
//					CookieStore store = context.getCookieStore();
//					List<Cookie> cookieList = store.getCookies();
//
//					for (Cookie c : cookieList) {
//						if (c.getName().equals("JSESSIONID")) {
//							String sessionId = c.getValue();
//							jsonObj.put("sessionId", sessionId);
//						}
//					}
//				}
				return content;
			}
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * 构造请求头
	 * 
	 * @param params
	 * @return
	 */
	private Header[] buildHeader(Map<String, String> params) {

		if (null == params) {
			params = new HashMap<>();
		}
		params.put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		params.put(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
		params.put(HttpHeaders.ACCEPT, "*/*");
		Header[] headers = null;
		if (!CollectionUtils.isEmpty(params)) {
			headers = new BasicHeader[params.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
				i++;
			}
		}
		return headers;
	}

	/**
	 * 构建一般的请求实体
	 * 
	 * @param paramsMap
	 * @return
	 */
	private StringEntity buildEnntity(Map<String, Object> paramsMap) {

		List<NameValuePair> valuePairs = new LinkedList<>();

		if (!CollectionUtils.isEmpty(paramsMap)) {
			for (Map.Entry<String, Object> entity : paramsMap.entrySet()) {
				valuePairs.add((new BasicNameValuePair(entity.getKey(), String.valueOf(entity.getValue()))));
			}
		}
		StringEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
		return entity;
	}

	private String getResponse(HttpPost httpPost) throws ClientProtocolException, IOException {

		String content = null;
		HttpResponse http_response = null;
//		JSONObject jsonObj = new JSONObject();
		try {
			http_response = httpClient.execute(httpPost);
			if (http_response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(http_response.getEntity(), "UTF-8");
				return content.toString();
//				jsonObj = JSONObject.parseObject(content);
//				return jsonObj;
			}
		} finally {
			httpPost.releaseConnection();
		}
		return null;
	}
}

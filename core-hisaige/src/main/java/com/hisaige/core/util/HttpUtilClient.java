package com.hisaige.core.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
/**
 * 发送http请求工具类
 * @author chenyj
 * @date 2019年7月11日
 */
public class HttpUtilClient {

	private static Logger logger = LoggerFactory.getLogger(HttpUtilClient.class);
	private CloseableHttpClient httpClient;
	
	/**
	 * 创建不使用代理的httpclient，不没有代理一般不能用抓包工具抓包
	 */
	public HttpUtilClient(){
		
//		this.httpClient = HttpClients.createDefault();
		RequestConfig config = RequestConfig.custom().setConnectTimeout(25000).build(); //统一设置超时25S
		this.httpClient= HttpClientBuilder.create().setDefaultRequestConfig(config).build();
//		this.httpClient = HttpClientBuilder.create().build();
	}
	
	/**
	 * 建立存在代理的HttpClient对象
	 * @param proxyIP 代理IP地址
	 * @param port 代理端口号
	 */
	public HttpUtilClient(String proxyIP, int port){

		HttpHost proxy = new HttpHost(proxyIP, port);
		RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(25000).build();
		this.httpClient= HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}
	/**
	 * 重要，使用完毕应关闭httpclient
	 */
	public void closeHttpClient(){
		
		if(this.httpClient != null){
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 一般的POST请求
     * @param uri
     * @param paramsMap
     * @param headereMap
     * @return
     */
    public JSONObject doPost(String uri, Map<String, Object> paramsMap, Map<String,String> headereMap){

        HttpPost httpPost = new HttpPost(uri);//建立Post对象

		Header[] headers = buildHeader(headereMap);//设置请求头
        httpPost.setHeaders(headers);
        StringEntity entity =  buildEnntity(paramsMap);//请求实体
        httpPost.setEntity(entity);
        logger.debug("request uri->{}, request header->{}, request params->{}", uri, headereMap, paramsMap);
        return getResponse(httpPost);

    }
    /**
     * 一般的Get请求
     * @param uri
     * @param paramsMap 请求参数
     * @param headereMap 请求头参数
     * @return JSONObject 
     */
    public JSONObject doGet(String uri, Map<String,String> paramsMap, Map<String,String> headereMap){
    	uri += "?";
    	//设置请求体
    	List<NameValuePair> params = Lists.newArrayList();
		if(null != paramsMap){
			for(Entry<String, String> entry:paramsMap.entrySet()){
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));	
			}
		}
        HttpResponse response;
        HttpGet httpGet = null;
		try {
			
			HttpClientContext context = HttpClientContext.create();// 创建HttpClient上下文
			
			uri += EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));//构建请求体
			httpGet = new HttpGet(uri);//建立Get对象
			Header[] headers = null;
			if(null != headereMap){
				headers = buildHeader(headereMap);//设置请求头
			}
			httpGet.setHeaders(headers);
			response = httpClient.execute(httpGet, context);
			String content = EntityUtils.toString(response.getEntity(),Consts.UTF_8 );
			JSONObject jsonObj = JSONObject.parseObject(content);
//			logger.debug("user login..." );
			
			if (content != null && !content.equals("") && response.getStatusLine().getStatusCode() == 200) {
				if(uri.contains("login")){
					CookieStore store = context.getCookieStore();
					List<Cookie> cookieList = store.getCookies();
				
					for (Cookie c : cookieList) {
						if(c.getName().equals("JSESSIONID")){
							String sessionId = c.getValue();
							jsonObj.put("sessionId", sessionId);
						}
					}
				}
				return jsonObj;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(httpGet != null){
				httpGet.releaseConnection();
			}
        }
    	return null;
    }

    /**
     * 构造请求头
     * @param params
     * @return
     */
    private Header[] buildHeader(Map<String, String> params){
    	
//        params.put(HttpHeaders.USER_AGENT,"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
//        params.put(HttpHeaders.ACCEPT_ENCODING,"gzip, deflate");
//        params.put(HttpHeaders.ACCEPT,"*/*");
        Header[] headers = null;
        if(params != null && params.size() > 0){
            headers = new BasicHeader[params.size()];
            int i  = 0;
            for (Entry<String, String> entry:params.entrySet()) {
                headers[i] = new BasicHeader(entry.getKey(),entry.getValue());
                i++;
            }
        }
        return headers;
    }

    /**
     * 构造请求实体
     * @param paramsMap
     * @return
     */
    private StringEntity buildEnntity(Map<String, Object> paramsMap){


        List<NameValuePair> valuePairs = new LinkedList<>();
    
        if(paramsMap != null && paramsMap.size() > 0){
            for(Entry<String,Object> entity:paramsMap.entrySet()){
                valuePairs.add((new BasicNameValuePair(entity.getKey(),String.valueOf(entity.getValue()))));
            }
        }
        StringEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
//        entity.setContentType("applicaxtion/x-www-form-urlencoded");
//            entity.setContentType("application/json");
        return entity;
    }

	/**
	 * 以raw形式发送post请求，如果字符串参数不是json而是数组，则paramsMap的键设置为字符串“null”,值为字符串参数
	 *
	 * @param uri
	 * @param entityStr
	 * @param headerMap
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public JSONObject doRawPost(String uri, String entityStr, Map<String, String> headerMap)
			throws ClientProtocolException, IOException {

		HttpPost httpPost = new HttpPost(uri);// 建立Post对象

		Header[] headers = buildHeader(headerMap);// 设置请求�?
		httpPost.setHeaders(headers);

		StringEntity entity = null;// = buildRawEnntity(paramsMap);//请求实体
		entity = new StringEntity(entityStr, Consts.UTF_8);
		entity.setContentType("application/json; charset=utf-8");
		httpPost.setEntity(entity);

		return getResponse(httpPost);
	}

    private JSONObject getResponse(HttpPost httpPost){

        String content = null;
        HttpResponse http_response = null;
        JSONObject jsonObj = new JSONObject();
        try {
        	http_response = httpClient.execute(httpPost);
            content = EntityUtils.toString(http_response.getEntity(),"UTF-8");
     
            if(content != null && !content.equals("") &&  !content.contains("\"code\":\"-") && http_response.getStatusLine().getStatusCode() == 200){
            	jsonObj = JSONObject.parseObject(content);
//            	logger.debug(httpPost.getURI() + " response code--> " + jsonObj.get("desc"));
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	httpPost.releaseConnection();
        }
        return jsonObj;
    }
}

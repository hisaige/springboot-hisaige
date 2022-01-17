package com.hisaige.core.httpclient.common;

import com.alibaba.fastjson.JSON;
import com.hisaige.core.util.CollectionUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author chenyj
 * 2019/10/16 - 17:27.
 **/
public class HttpRequest extends AbstractHttpRequest {

    @Override
    public HttpRes get(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 本方法主要用于心跳，http超时时间设置比较短
     * @param url get请求
     * @return HttpRes
     * @throws IOException 心跳有异常则抛出
     */
    public HttpRes heartbeatGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        super.setHttpClient(HttpClientManager.getInstance().getHeartbeatHttpClient());
        return execute(httpGet);
    }

    /**
     * 使用代理模式发送get请求
     * @param url 请求地址
     * @param ip 代理ip
     * @param port 代理端口
     * @return HttpRes
     * @throws IOException 有异常抛出
     */
    public HttpRes proxyGet(String url, String ip, int port) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        super.setHttpClient(HttpClientManager.getInstance().getProxyHttpClient(ip, port));
        return execute(httpGet);
    }

    @Override
    public HttpRes post(String url) throws IOException {

        HttpPost httpPost = new HttpPost(url);
        return execute(httpPost);
    }

    @Override
    public HttpRes postJson(String url, String jsonDate) throws IOException {
        assert JSON.isValid(jsonDate);
        HttpPost httpPost = new HttpPost(url);
        //构造普通方式post请求
        StringEntity strEntity = new StringEntity(jsonDate, Consts.UTF_8.name());
        strEntity.setContentEncoding(Consts.UTF_8.name());
        strEntity.setContentType(ContentType.APPLICATION_JSON.toString());
        httpPost.setEntity(strEntity);
        return execute(httpPost);
    }

    @Override
    public HttpRes postFile(String url, File file, String name) throws IOException {

        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(name, file, ContentType.MULTIPART_FORM_DATA, file.getName());
        return upload(httpPost, builder);
    }

    @Override
    public HttpRes postStream(String url, InputStream stream, String fileName, String name) throws IOException {

        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(name, stream, ContentType.APPLICATION_OCTET_STREAM, fileName);
        return upload(httpPost, builder);
    }

    @Override
    public HttpRes postByte(String url, byte[] bytes, String fileName, String name) throws IOException {

        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(name, bytes, ContentType.MULTIPART_FORM_DATA, fileName);
        return upload(httpPost, builder);
    }


    @Override
    public HttpRes put(String url, String jsonDate) {
        throw new NotImplementedException();
    }

    @Override
    public HttpRes delete(String url, String jsonDate) {
        throw new NotImplementedException();
    }

    /**
     * 执行请求方法封装
     * @param httpRequestBase 主要是get或post请求
     * @return HttpRes 不可能为null值
     * @throws IOException 异常直接抛出
     */
    @Override
    protected HttpRes execute(HttpRequestBase httpRequestBase) throws IOException {
        HttpRes httpRes = new HttpRes();
        HttpEntity entity = null;
        setHeader(HttpHeaders.CONTENT_ENCODING, Consts.UTF_8.name());
        initRequest(httpRequestBase);
        try {
            CloseableHttpClient httpClient = super.getHttpClient();
            CloseableHttpResponse response = httpClient.execute(httpRequestBase);
            httpRes.setHttpResponse(response);
            entity = response.getEntity();
            if (null != entity) {
                httpRes.setResBody(EntityUtils.toString(entity, Consts.UTF_8));
                StatusLine statusLine = response.getStatusLine();
                if(null != statusLine) {
                    httpRes.setHttpStatus(statusLine.getStatusCode());
                }
            }
            printResult(httpRequestBase, httpRes);
        } catch (IOException e) {
            handleResponseStatus(httpRequestBase, httpRes, e);
        } finally{
            EntityUtils.consume(entity);
        }
        return httpRes;
    }


    /**
     * 构造entity
     * @param paramsMap 参数map
     * @return StringEntity
     */
    public static StringEntity buildEnntity(Map<String, Object> paramsMap){

        List<NameValuePair> valuePairs = new LinkedList<>();

        if(paramsMap != null && paramsMap.size() > 0){
            for(Map.Entry<String,Object> entity:paramsMap.entrySet()){
                valuePairs.add((new BasicNameValuePair(entity.getKey(),String.valueOf(entity.getValue()))));
            }
        }
        return new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
    }

    public static StringEntity buildListEnntity(Map<String, List<Object>> paramsMap){

        List<NameValuePair> valuePairs = new LinkedList<>();

        List<Object> params;
        if(paramsMap != null && paramsMap.size() > 0){
            for(Map.Entry<String, List<Object>> entity:paramsMap.entrySet()){
                params = entity.getValue();
                if(!CollectionUtils.isEmpty(params)){
                    params.forEach(param -> valuePairs.add((new BasicNameValuePair(entity.getKey(), String.valueOf(param)))));
                }
            }
        }
        return new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
    }
}

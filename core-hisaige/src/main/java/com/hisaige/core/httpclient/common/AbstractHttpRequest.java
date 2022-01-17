package com.hisaige.core.httpclient.common;

import com.hisaige.core.util.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http请求方法封装
 * @author chenyj
 * 2019/10/16 - 16:40.
 **/
public abstract class AbstractHttpRequest implements IHttpReqest {

    private Logger logger = LoggerFactory.getLogger(AbstractHttpRequest.class);

    private Map<String, String> header = new ConcurrentHashMap<>();
    private HttpEntity entity;
    private CloseableHttpClient httpClient = HttpClientManager.getInstance().getHttpClient();

    /**
     * 初始化连接 设置header和entity
     * 如果是get请求 不支持设置entity
     * @param request HttpRequestBase
     */
    void initRequest(HttpRequestBase request) {

        //httpGet请求不支持设置entity
        if (request instanceof HttpEntityEnclosingRequestBase) {
            if (null != entity) {
                System.out.println(entity);
                ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
            }
        }
        if (!header.containsKey(HttpHeaders.CONTENT_ENCODING)) {
            header.put(HttpHeaders.CONTENT_ENCODING, Consts.UTF_8.name());
        }
        for (String key : header.keySet()) {
            request.setHeader(key, header.get(key));
        }
    }

    //根据url判断是否打印异常信息
    private boolean isHandle(String url){
        return !url.contains("heartbeat");
    }

    void handleResponseStatus(HttpRequestBase httpRequestBase, HttpRes httpRes, Exception ex) {

        assert null != httpRequestBase;
        if (ex instanceof SocketException) {
            //默认404
            httpRes.setHttpStatus(HttpStatus.SC_GATEWAY_TIMEOUT);
        }
        httpRes.setErrorMsg(ex.getMessage());
        if(isHandle(httpRequestBase.getURI().toString())){
            logger.warn("http request throw exception, method:{} -> url:{}, headers:{}, \r\nresponse:{}",httpRequestBase.getMethod(), httpRequestBase.getURI(), Arrays.asList(Optional.ofNullable(httpRequestBase.getAllHeaders()).orElse(new Header[] {})), httpRes);
        } else {
            logger.trace("http request throw exception, method:{} -> url:{}, headers:{}, \r\nresponse:{}",httpRequestBase.getMethod(), httpRequestBase.getURI(), Arrays.asList(Optional.ofNullable(httpRequestBase.getAllHeaders()).orElse(new Header[] {})), httpRes);
        }
    }

    void printResult(HttpRequestBase httpRequestBase, HttpRes httpRes){
        if(isHandle(httpRequestBase.getURI().toString())){
            //优先打印字符串数据
            String resBody = httpRes.getResBody();
            if(StringUtils.isEmpty(resBody)) {
                //打印byte数据，只打印http返回的字符串结果
                resBody = Arrays.toString(httpRes.getResBytes());
            }
            if(null != resBody && resBody.length() > 500) {
                //限制日志长度500字符
                resBody = resBody.substring(0, 500) + "...";
            }
            String reqEntityParams = null;
            if(httpRequestBase instanceof HttpPost) {
                HttpEntity reqEntity = ((HttpPost) httpRequestBase).getEntity();
                try {
                    if(null != reqEntity){
                        reqEntityParams = EntityUtils.toString(reqEntity, Consts.UTF_8);
                    }
                } catch (ParseException | IOException e) {}
            }
            if(!StringUtils.isEmpty(reqEntityParams)) {
                logger.debug("method:{} -> url:{}, headers:{}, httpStatus:{}, reqParams:{}, \r\nresponse:{}",httpRequestBase.getMethod(), httpRequestBase.getURI(), Arrays.asList(Optional.ofNullable(httpRequestBase.getAllHeaders()).orElse(new Header[] {})), httpRes.getHttpStatus(), reqEntityParams, resBody);
            } else {
                logger.debug("method:{} -> url:{}, headers:{}, httpStatus:{}, \r\nresponse:{}",httpRequestBase.getMethod(), httpRequestBase.getURI(), Arrays.asList(Optional.ofNullable(httpRequestBase.getAllHeaders()).orElse(new Header[] {})), httpRes.getHttpStatus(), resBody);
            }
        }
    }

    protected HttpRes upload(HttpPost httpPost, MultipartEntityBuilder multipartEntityBuilder) throws IOException {

        HttpEntity entity = multipartEntityBuilder.build();
        httpPost.setEntity(entity);
        return execute(httpPost);
    }

    protected abstract HttpRes execute(HttpRequestBase httpRequestBase) throws IOException;

    public void setHeader(String headerName, String headerValue) {
        this.header.put(headerName, headerValue);
    }
    public void setHeader(Header header) {
        this.header.put(header.getName(), header.getValue());
    }
    public synchronized void setEntity(HttpEntity entity){
        this.entity = entity;
    }

    CloseableHttpClient getHttpClient() {
        return httpClient;
    }
    void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

}

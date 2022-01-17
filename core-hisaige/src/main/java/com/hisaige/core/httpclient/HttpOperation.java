package com.hisaige.core.httpclient;

import com.hisaige.core.entity.constant.CoreConstants;
import com.hisaige.core.httpclient.common.HttpRequest;
import com.hisaige.core.httpclient.common.HttpRes;
import com.hisaige.core.util.CollectionUtils;
import com.hisaige.core.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author chenyj
 * 2019/10/17 - 10:34.
 **/
public class HttpOperation {

    /**
     * get请求
     * @param url 请求地址
     * @param headers 请求头
     * @return HttpRes
     * @throws IOException 请求有异常则抛出
     */
    public static HttpRes doGet(String url, List<Header> headers) throws IOException {

        assert !StringUtils.isEmpty(url);
        HttpRequest httpRequest = getHttpRequest(headers);
        return httpRequest.get(url);
    }

    /**
     * get请求
     * @param url 请求地址
     * @param headers 请求头
     * @return HttpRes
     * @throws IOException 请求有异常则抛出
     */
    public static HttpRes doGet(String url, List<Header> headers, Map<String, String> params) throws IOException {

        if(null != params && !params.isEmpty()){
            //设置url参数
            StringBuilder sb = new StringBuilder("?");
            for(Map.Entry<String, String> entry:params.entrySet()){
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), CoreConstants.UTF_8)).append("&");
            }
            url += sb.substring(0, sb.length() - 2);
        }

        assert !StringUtils.isEmpty(url);
        HttpRequest httpRequest = getHttpRequest(headers);
        return httpRequest.get(url);
    }

    public static HttpRes heartbeatGet(String url, List<Header> headers) throws IOException {

        assert !StringUtils.isEmpty(url);
        HttpRequest httpRequest = getHttpRequest(headers);
        return httpRequest.heartbeatGet(url);
    }

    public static HttpRes proxyGet(String url, List<Header> headers, String ip, int port) throws IOException {

        assert !StringUtils.isEmpty(url);
        assert !StringUtils.isEmpty(ip);
        HttpRequest httpRequest = getHttpRequest(headers);
        return httpRequest.proxyGet(url, ip, port);
    }

    private static HttpRequest getHttpRequest(List<Header> headers) {
        HttpRequest httpRequest = new HttpRequest();
        if(!CollectionUtils.isEmpty(headers)){
            headers.forEach(httpRequest::setHeader);
        }
        return httpRequest;
    }

    public static HttpRes doPost(String url, HttpEntity httpEntity, List<Header> headers) throws IOException {

        assert !StringUtils.isEmpty(url);
        HttpRequest httpRequest = new HttpRequest();

        if(null != httpEntity){
            httpRequest.setEntity(httpEntity);
        }
        if(!CollectionUtils.isEmpty(headers)){
            headers.forEach(httpRequest::setHeader);
        }
        return httpRequest.post(url);
    }

    public static HttpRes doPostJson(String url, List<Header> headers, String jsonData) throws IOException {

        assert !StringUtils.isEmpty(url);

        HttpRequest httpRequest = new HttpRequest();

        httpRequest.setHeader(HttpHeaders.CONNECTION, "keep-Alive");

        if(!CollectionUtils.isEmpty(headers)){
            headers.forEach(httpRequest::setHeader);
        }
        return httpRequest.postJson(url, jsonData);
    }

    public static HttpRes doUploadFile(String url, String fileParam, File file) throws IOException {

        //文件参数，如@RequestParam("file") MultipartFile multipartFile，参数就是file
        if(StringUtils.isEmpty(fileParam)){
            fileParam = "file";
        }
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeader(HttpHeaders.CONNECTION, "keep-Alive");

        return httpRequest.postFile(url, file, fileParam);
    }

    /**
     *
     * @param url 上传地址
     * @param fileParam 文件参数，如@RequestParam("file") MultipartFile multipartFile，参数就是file
     * @param fileName 文件名
     * @param bytes 文件字节
     * @return  HttpRes
     * @throws IOException 异常往上抛
     */
    public static HttpRes doUploadBytes(String url, String fileParam, String fileName, byte[] bytes) throws IOException {

        if(StringUtils.isEmpty(fileParam)){
            fileParam = "file";
        }
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeader(HttpHeaders.CONNECTION, "keep-Alive");

        return httpRequest.postByte(url, bytes, fileName, fileParam);
    }

    public static HttpRes doUploadStream(String url, String fileParam, String fileName, InputStream inputStream) throws IOException {

        if(StringUtils.isEmpty(fileParam)){
            fileParam = "file";
        }
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeader(HttpHeaders.CONNECTION, "keep-Alive");

        return httpRequest.postStream(url, inputStream, fileName, fileParam);
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        HttpRes doGet = HttpOperation.doGet("http://10.82.25.221:8090/aasdsads", null);
        System.out.println(System.currentTimeMillis() - start);
    }
}

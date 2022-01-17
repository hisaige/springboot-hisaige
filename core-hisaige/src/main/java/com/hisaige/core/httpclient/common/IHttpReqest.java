package com.hisaige.core.httpclient.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * http相关请求方法
 */
public interface IHttpReqest {

    /**
     * 普通get请求
     * @param url get请求
     * @return String字符串
     */
    HttpRes get(String url) throws IOException;

    /**
     * 普通post请求
     * @param url 请求地址
     * @return HttpRes
     */
    HttpRes post(String url) throws IOException;

    /**
     * 请求体为json字符串的post请求
     * @param url 请求地址
     * @param jsonDate json格式数据请求体
     * @return HttpRes
     * @throws IOException 关闭httpPost有异常抛出
     */
    HttpRes postJson(String url, String jsonDate) throws IOException;

    HttpRes postFile(String url, File file, String name) throws IOException;

    HttpRes postStream(String url, InputStream stream, String fileName, String name) throws IOException;

    HttpRes postByte(String url, byte[] bytes, String fileName, String name) throws IOException;

    HttpRes put(String url, String jsonDate);

    HttpRes delete(String url, String jsonDate);
}

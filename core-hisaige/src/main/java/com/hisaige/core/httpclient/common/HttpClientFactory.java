package com.hisaige.core.httpclient.common;

/**
 * @author chenyj
 * 2019/10/16 - 11:51.
 **/
public class HttpClientFactory {
    private static volatile HttpClientFactory httpClientFactory;

    /**
     * 初始化工厂
     * @return HttpClientFactory
     */
    public static HttpClientFactory getInstance(){
        if (null == httpClientFactory) {
            synchronized (HttpClientFactory.class) {
                if (null == httpClientFactory) {
                    httpClientFactory = new HttpClientFactory();
                }
            }
        }
        return httpClientFactory;
    }




}

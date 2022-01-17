package com.hisaige.core.httpclient.common;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

/**
 * @author chenyj
 * 2019/10/16 - 13:21.
 **/
public class HttpClientManager {

    private static volatile CloseableHttpClient defaultHttpClient = null;
    private static volatile CloseableHttpClient heartbeatHttpClient = null;

    // 异步httpclient
    private CloseableHttpAsyncClient asyncHttpClient;

    // 异步加代理的httpclient
    private CloseableHttpAsyncClient proxyAsyncHttpClient;

    private static volatile HttpClientManager httpClientManager;

    private static final int maxTotal = 15000;
    private static final int maxPerRoute = 200;

    private static final int MaxHeartbeatTotal = 100;


    private PoolingHttpClientConnectionManager connectionManager;

    private PoolingHttpClientConnectionManager heartbeatConnectionManager;


    private PoolingHttpClientConnectionManager asyncConnectionManager;


    private RequestConfig defaultRequestConfig;

    private RequestConfig asyncRequestConfig;

    private RequestConfig heartbeatRequestConfig;

    private ConnectionKeepAliveStrategy keepAliveStrategy;

    /**
     * 初始化httpClient管理器,连接时间设置得比较长的话 不适用于心跳，
     * 如果要用于心跳，需要修改connectionManager配置,或者再创建一个单独用于心跳的PoolingHttpClientConnectionManager
     */
    private HttpClientManager() {
        connectionManager = new PoolingHttpClientConnectionManager();
        heartbeatConnectionManager = new PoolingHttpClientConnectionManager();
        RequestConfig.Builder defaultBuilder = RequestConfig.custom()
                .setSocketTimeout(60000)//等待返回
                .setConnectTimeout(5000)//建立连接
                .setConnectionRequestTimeout(3000);//从连接池获取连接
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        connectionManager.setValidateAfterInactivity(30000);

        heartbeatRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(3000)
                .build();
        heartbeatConnectionManager.setMaxTotal(MaxHeartbeatTotal);
        heartbeatConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
        heartbeatConnectionManager.setValidateAfterInactivity(30000);

        //用于同步请求
        defaultRequestConfig = defaultBuilder.build();

        //用于异步请求
        asyncRequestConfig = defaultBuilder.build();

        keepAliveStrategy = (response, context) -> {
            Args.notNull(response, "HTTP response");
            final HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                final HeaderElement he = it.nextElement();
                final String param = he.getName();
                final String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (final NumberFormatException ignore) {
                    }
                }
            }
            return 1;
        };
    }

    public static HttpClientManager getInstance() {
        if (null == httpClientManager) {
            synchronized (HttpClientManager.class) {
                if (null == httpClientManager) {
                    httpClientManager = new HttpClientManager();
                }
            }
        }
        return httpClientManager;
    }

    /**
     * 用于业务（非心跳的httpClient）
     *
     * @return 用于业务的httpClient
     */
    CloseableHttpClient getHttpClient() {
        if (null == defaultHttpClient) {
            synchronized (HttpClientManager.class) {
                if (null == defaultHttpClient) {
                    defaultHttpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).setKeepAliveStrategy(keepAliveStrategy).build();
                }
            }
        }
        return defaultHttpClient;
    }

    /**
     * 用于心跳的httpClient,其配置超时时间，最大连接数 等配置比较小
     *
     * @return 用于心跳的httpClient
     */
    CloseableHttpClient getHeartbeatHttpClient() {
        if (null == heartbeatHttpClient) {
            synchronized (HttpClientManager.class) {
                if (null == heartbeatHttpClient) {
                    heartbeatHttpClient = HttpClients.custom().setConnectionManager(heartbeatConnectionManager).setDefaultRequestConfig(heartbeatRequestConfig).setKeepAliveStrategy(keepAliveStrategy).build();
                    return heartbeatHttpClient;
                }
            }
        }
        return heartbeatHttpClient;
    }

    /**
     * 如果需要用到 CloseableHttpClient 来做测试任务 可以使用此方法生成的httpClient
     * 如 希望使用fiddler抓包，可以设置代理端口8888（fiddler默认端口）
     * 注意 代理服务必须存在才能运行，否则会报错，例如 proxyIP=127.0.0.1 port=8888，如果关闭了fiddler则会报错
     *
     * @param proxyIP 代理ip 如127.0.0.1
     * @param port    代理端口 如8888
     * @return CloseableHttpClient
     */
    public CloseableHttpClient getProxyHttpClient(String proxyIP, int port) {
        HttpHost proxy = new HttpHost(proxyIP, port);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(20000).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(config).setKeepAliveStrategy(keepAliveStrategy).build();
    }

}

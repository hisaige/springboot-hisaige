package com.hisaige.core.httpclient.common;

import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * @author chenyj
 * 2019/10/16 - 16:43.
 **/
@Data
public class HttpRes {
    //请求状态码 默认404
    private int httpStatus = HttpStatus.SC_NOT_FOUND;

    //http返回的结构，可以从中提取http请求返回的各种信息
    private HttpResponse httpResponse;

    //请求返回字符串结果
    private String resBody;

    //请求过程产生的错误信息
    private String errorMsg;

    //如果请求的是字节数组，请求结果在这里
    private byte[] resBytes;
}

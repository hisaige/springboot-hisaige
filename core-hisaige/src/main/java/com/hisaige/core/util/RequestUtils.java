package com.hisaige.core.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hisaige.core.entity.constant.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author chenyj
 * 2020/6/18 - 20:11.
 **/
public class RequestUtils {

    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * 获取客户端IP
     * @return IP
     */
    public static String getReqIp(){
        return IpUtils.getRequestIp();
    }

    /**
     * 获取用户名，注意配合实际项目使用
     * @return String
     */
    public static String getReqUserName(){
        HttpServletRequest request = getRequest();
        if(null == request){
            return null;
        }
        String userName = request.getHeader("userName");
        if(StringUtils.isEmpty(userName)){
            userName = (String) request.getAttribute("userName");
        }
        return userName;
    }

    /**
     * 获取客户端IP
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(null != requestAttributes){
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    /**
     * 获取url路径
     *
     * @param uriStr 路径
     * @return url路径
     */
    public static String getPath(String uriStr) {
        URI uri;

        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException var3) {
            throw new RuntimeException(var3);
        }
        return uri.getPath();
    }


    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request request
     * @return 字符串内容
     */
    public static String getRequestParamString(HttpServletRequest request, Boolean isJsonType) {
        try {
            return getRequestStr(request, isJsonType);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestStr(HttpServletRequest request, boolean isJson) throws IOException {
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            queryString = new String(queryString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("&amp;", "&").replaceAll("%22", "\"");
        } else {
            queryString = getRequestStr(request, getRequestBytes(request));
        }
        if(isJson) {
            queryString = getJsonStrByQueryUrl(queryString);
        }
        return queryString;
    }

    /**
     * 获取 request 请求的 byte[] 数组
     *
     * @param request request
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] getRequestBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @param buffer buffer
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestStr(HttpServletRequest request, byte[] buffer) throws IOException {
        String str = reqBuffer(request, buffer);
        if (StringUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String key = parameterNames.nextElement();
                String value = request.getParameter(key);
                sb.append(key).append("=").append(value).append("&");
            }
            str = sb.toString();
            str = str.substring(0, str.length() - 1);
        }
        return str.replaceAll("&amp;", "&");
    }

    private static String reqBuffer(HttpServletRequest request, byte[] buffer) throws UnsupportedEncodingException {
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = CoreConstants.UTF_8;
        }
        return new String(buffer, charEncoding).trim();
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @param buffer buffer
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestJsonStr(HttpServletRequest request, byte[] buffer) throws IOException {
        String str = reqBuffer(request, buffer);
        if (StringUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder();
            Enumeration<String> parameterNames = request.getParameterNames();
            JSONObject jsonObject = new JSONObject();
            while (parameterNames.hasMoreElements()) {
                String key = parameterNames.nextElement();
                String value = request.getParameter(key);
                jsonObject.put(key, value);
            }
            str = jsonObject.toJSONString();
        }
        return str;
    }

    /**
     * 将url请求参数转化为json
     * @param paramStr url请求参数
     * @return 参数的json格式
     */
    public static String getJsonStrByQueryUrl(String paramStr){
        //String paramStr = "a=a1&b=b1&c=c1";
        String[] params = paramStr.split("&");
        JSONObject obj = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                try {
                    obj.put(key,value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj.toString();
    }
}

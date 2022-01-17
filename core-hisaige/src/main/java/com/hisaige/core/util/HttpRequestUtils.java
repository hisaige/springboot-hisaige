package com.hisaige.core.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author chenyj
 * 2019/11/20 - 10:33.
 **/
public class HttpRequestUtils {

    public static HttpServletRequest getRequest(){
        HttpServletRequest request = null;
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if(ra instanceof ServletRequestAttributes) {
            ServletRequestAttributes sra = (ServletRequestAttributes)ra;
            request = sra.getRequest();
        }
        return request;
    }

    /**
     * 获取json参数
     * @return http请求中的json参数
     * @throws IOException 异常往上抛
     */
    public static String getJsonPrams() throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            sb.append(inputStr);
        }
        return sb.toString();
    }

}

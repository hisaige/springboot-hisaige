package com.hisaige.core.util;

import com.alibaba.fastjson.JSON;
import com.hisaige.core.entity.constant.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author chenyj
 * 2020/6/28 - 22:54.
 **/
public class ResponseUtils {
    private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);

    public static void writeObj2Json(Object object){

        HttpServletResponse response = getResponse();
        if(null == response){
            logger.warn("response is null...");
            return;
        }
        response.setCharacterEncoding(CoreConstants.UTF_8);
        response.setContentType(CoreConstants.APPLICATION_JSON);
        try(PrintWriter writer = response.getWriter()){
            writer.write(JSON.toJSONString(object));
            writer.flush();
        } catch (IOException e) {
            logger.error("writeObj2Json error...", e);
        }
    }

    @Nullable
    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getResponse();
        }
        return null;
    }
}

package com.hisaige.core.entity.constant;

import sun.management.Agent;
import sun.management.resources.agent;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 常量池
 * @author chenyj
 * 2019/11/20 - 11:15.
 **/
public interface CoreConstants {

    //http header
    String APPLICATION_JSON = "application/json; charset=utf-8";
    String USER_AGENT = "User-Agent";

    //字符编码
    String GBK = "GBK";
    //编码 必要时可以结合这个类使用 StandardCharsets
    String UTF_8 = "UTF-8";
    Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    String COMMA = ",";
    String DOT = ".";
    String COLON = ":";

    //文件夹
    String COMMONPATH = "common";
}

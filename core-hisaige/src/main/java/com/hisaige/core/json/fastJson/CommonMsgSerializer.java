package com.hisaige.core.json.fastJson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author chenyj
 * 2019/12/15 - 0:33.
 **/
public class CommonMsgSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if(null == object){
            serializer.write("");
        } else {
            serializer.write(object);
        }
    }
}

package com.hisaige.core.entity.res;

import com.hisaige.core.entity.enums.ReturnCodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用静态返回方法
 * @author chenyj
 * 2020/9/13 - 13:18.
 **/
public class R extends AjaxMessageRes<Map<String, Object>> {

    //把构造方法私有
    private R() {}

    public static R ok(){
        return new R();
    }

    //服务异常
    public static R error() {
        R r = new R();
        r.setCode(ReturnCodeEnum.SERVER_EXCEPTION.getCode());
        r.setDesc("failed");
        return r;
    }

    public R error(ReturnCodeEnum returnCodeEnum){
        this.setCode(returnCodeEnum.getCode());
        this.setDesc(returnCodeEnum.getDesc_EN());
        return this;
    }

    public R desc(String desc){
        this.setDesc(desc);
        return this;
    }

    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value){
        if(null == getMsg()){
            synchronized (this){
                if(null == getMsg()){
                    setMsg(new HashMap<>());
                }
            }
        }
        this.getMsg().put(key, value);
        return this;
    }

    public R data(Map<String, Object> map){
        this.setMsg(map);
        return this;
    }

}

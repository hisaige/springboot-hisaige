package com.hisaige.core.entity.res;

/**
 * @author chenyj
 * 2019/9/21 - 17:02.
 **/
public class AjaxMessageRes<T> {
    private Integer code = 0;
    private String desc = "success";
    private T msg;

    public AjaxMessageRes() {
    }

    public AjaxMessageRes(T t) {
        this.msg = t;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }
}

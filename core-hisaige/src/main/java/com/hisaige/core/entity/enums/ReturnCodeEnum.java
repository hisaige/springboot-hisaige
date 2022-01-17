package com.hisaige.core.entity.enums;

public enum ReturnCodeEnum {

    //通用配置
    ILLEGALARGUMENT_EXCEPTION(-400, "illegal argument", "请求参数错误"),
    NOTLOGIN_EXCEPTION(-401, "not logged in", "未登录"),
    PERMISSION_DENY(-403, "permission deny", "权限不足"),
    SERVER_EXCEPTION(-10001, "server exception", "系统异常"),
    FILE_UPLOAD_EXCEPTION(-10003, "file upload error", "文件上传失败"),
    REQUEST_LIMIT_EXCEPTION(-99999, "request limited", "接口限流"),

    //用户配置
    USER_NOT_EXIST(-11001, "user not exist", "用户信息不存在"),
    ROLE_NOT_EXIST(-11002, "role not exist", "角色信息不存在"),
    MENU_NOT_EXIST(-11003, "menu not exist", "菜单信息不存在"),
    LOGIN_FAILED(-11005, "invalid userName or password", "用户名或密码错误"),
    TOKEN_EXPIRED(-11006, "token expired", "token已过期"),
    ;
    private Integer code;
    private String desc_EN;
    private String desc_CN;

    ReturnCodeEnum(Integer code, String desc_EN, String desc_CN){
        this.code = code;
        this.desc_EN = desc_EN;
        this.desc_CN = desc_CN;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc_EN() {
        return desc_EN;
    }

    public String getDesc_CN() {
        return desc_CN;
    }

    public void setDesc_EN(String desc_EN) {
        this.desc_EN = desc_EN;
    }

    public void setDesc_CN(String desc_CN) {
        this.desc_CN = desc_CN;
    }
}

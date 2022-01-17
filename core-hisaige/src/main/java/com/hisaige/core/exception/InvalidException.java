package com.hisaige.core.exception;

import com.hisaige.core.entity.enums.ReturnCodeEnum;
import com.hisaige.core.util.StringUtils;

/**
 * @author chenyj
 * 2019/9/21 - 17:19.
 **/
public class InvalidException extends Exception {

    private ReturnCodeEnum returnCodeEnum;
    private String desc;

    public InvalidException(ReturnCodeEnum returnCodeEnum){
        super(returnCodeEnum.getDesc_EN());
        this.returnCodeEnum = returnCodeEnum;
    }

    //重载构造器，添加额外的错误描述,注意 如果desc未经过国际化处理，则最好还是英文比较好
    public InvalidException(ReturnCodeEnum returnCodeEnum, String desc){
        this(returnCodeEnum);
        if(!StringUtils.isEmpty(desc)){
            this.desc = "(" + desc + ")";
        }
    }

    public ReturnCodeEnum getReturnCodeEnum() {
        return returnCodeEnum;
    }

    public String getDesc() {
        return desc;
    }
}

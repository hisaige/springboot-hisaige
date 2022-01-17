package com.hisaige.core.exception.handler;

import com.hisaige.core.entity.enums.ReturnCodeEnum;
import com.hisaige.core.entity.res.AjaxExceptionRes;
import com.hisaige.core.exception.InvalidException;
import com.hisaige.core.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * 捕获并处理controller异常
 *
 * @author chenyj
 * 2019/9/21 - 17:17.
 **/
//@ControllerAdvice 为方便测试微服务兜底方法，这里先将其注释掉不做controller层的异常处理
public class InvalidExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(InvalidExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AjaxExceptionRes handleException(Exception e) {
        logger.error(e.getMessage(), e);
        //这里主要处理JDK定义的异常，将其转化为自定义异常，自定义异常详见AjaxExceptionRes，
        if (e instanceof BindException) {
//            StringBuilder sb = new StringBuilder("[");
//            List<ObjectError> list = ((BindException) e).getAllErrors();
//            for (ObjectError item : list) {
//                sb.append(item.getDefaultMessage()).append(',');
//            }
//            sb.deleteCharAt(sb.length() - 1);
//            sb.append(']');
//            return new AjaxExceptionRes(new InvalidException(ReturnCodeEnum.ILLEGALARGUMENT_EXCEPTION, sb.toString()));
            List<ObjectError> errors = ((BindException) e).getAllErrors();
            StringBuilder errorMsg = new StringBuilder();
            if(!CollectionUtils.isEmpty(errors)){
                for (ObjectError error : errors) {
                    FieldError fieldError = (FieldError) error;
                    //Optional.ofNullable(error.getDefaultMessage()).orElse("").split(";")[0],这里仅面向前端提示第一个错误，其余信息通过日志打印
                    errorMsg.append(",").append(fieldError.getField()).append(":").append(Optional.ofNullable(error.getDefaultMessage()).orElse("").split(";")[0]);
                }
            }
            return new AjaxExceptionRes(new InvalidException(ReturnCodeEnum.ILLEGALARGUMENT_EXCEPTION, errorMsg.toString().substring(1)));
        } else if (e instanceof IllegalArgumentException || e instanceof ParseException) {
            return new AjaxExceptionRes(new InvalidException(ReturnCodeEnum.ILLEGALARGUMENT_EXCEPTION, e.getMessage()));
        } else {
            return new AjaxExceptionRes(e);
        }
    }

}

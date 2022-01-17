package com.hisiage.event.supper.notifyEvent.exception;

/**
 * 订阅失败异常
 * @author chenyj
 * 2020/4/4 - 17:39.
 **/
public class Subscribe2EventFailedException extends RuntimeException {

    public Subscribe2EventFailedException(){
        super();
    }

    public Subscribe2EventFailedException(String errorMsg){
        super(errorMsg);
    }
}

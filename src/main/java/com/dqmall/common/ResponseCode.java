package com.dqmall.common;

/**
 * @program: dqmall
 * @description: 返回码
 * @author: 唐庆阳
 * @create: 2019-09-24 13:54
 **/
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code,String desc)
    {
        this.code=code;
        this.desc=desc;
    }

    public int getCode()
    {
        return this.code;
    }

    public String getDesc(){
        return this.desc;
    }


}

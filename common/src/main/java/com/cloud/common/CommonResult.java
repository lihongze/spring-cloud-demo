package com.cloud.common;


import com.cloud.common.constant.ResponseCode;

/**
 * @author hongze
 * @date
 * @apiNote
 */
public class CommonResult<T> {
    private int code;

    private String msg;

    private T data;

    public CommonResult<T> setCode(ResponseCode responseCode) {
        this.code = responseCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public CommonResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public CommonResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public CommonResult<T> setData(T data) {
        this.data = data;
        return this;
    }
}

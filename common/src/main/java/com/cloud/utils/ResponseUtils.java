package com.cloud.utils;

import com.cloud.common.CommonResult;
import com.cloud.common.constant.ResponseCode;

/**
 * @author hongze
 * @date 2021-08-17 17:05:36
 * @apiNote
 */
public class ResponseUtils {
    private final static String SUCCESS = "success";

    public static <T> CommonResult<T> ok() {
        return new CommonResult<T>().setCode(ResponseCode.SUCCESS).setMsg(SUCCESS);
    }

    public static <T> CommonResult<T> ok(T data) {
        return new CommonResult<T>().setCode(ResponseCode.SUCCESS).setMsg(SUCCESS).setData(data);
    }

    public static <T> CommonResult<T> fail(String message) {
        return new CommonResult<T>().setCode(ResponseCode.FAIL).setMsg(message);
    }

    public static <T> CommonResult<T> makeRsp(int code, String msg) {
        return new CommonResult<T>().setCode(code).setMsg(msg);
    }

    public static <T> CommonResult<T> makeRsp(int code, String msg, T data) {
        return new CommonResult<T>().setCode(code).setMsg(msg).setData(data);
    }
}

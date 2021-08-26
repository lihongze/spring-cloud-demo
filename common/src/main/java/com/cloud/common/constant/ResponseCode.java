package com.cloud.common.constant;

/**
 * @author hongze
 * @date 2021-08-17 17:13:42
 * @apiNote
 */
public enum ResponseCode {
    // 成功
    SUCCESS(200),

    // 失败
    FAIL(400),

    // 接口不存在
    NOT_FOUND(404),

    // 服务器内部错误
    INTERNAL_SERVER_ERROR(500);

    public int code;

    ResponseCode(int code) {
        this.code = code;
    }
}

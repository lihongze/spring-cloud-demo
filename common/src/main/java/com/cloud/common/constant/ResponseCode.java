package com.cloud.common.constant;

/**
 * @author hongze
 * @date 2021-08-17 17:13:42
 * @apiNote
 */
public enum ResponseCode {
    // 成功
    SUCCESS(200,"请求成功"),

    // 失败
    FAIL(400, "请求失败"),

    // 接口不存在
    NOT_FOUND(404, "请求资源不存在"),

    // 服务器内部错误
    INTERNAL_SERVER_ERROR(500,"服务器内部错误"),

    TOO_QUICK(101,"访问过快");

    public int code;

    public String description;

    ResponseCode(int code,String description) {
        this.code = code;
        this.description = description;
    }
}

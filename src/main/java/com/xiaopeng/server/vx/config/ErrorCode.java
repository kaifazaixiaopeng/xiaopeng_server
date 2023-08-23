package com.xiaopeng.server.vx.config;

import lombok.Getter;

@Getter
public enum ErrorCode  {
    IMPORT_FAIL(10000, "文档校验异常"),
    PARSE_FAIL(10001, "文档解析异常"),

    IMPORT_FUTURE_DATE_FAIL(10002,"时间超过系统限制范围"),
    SUCCESS(200,"数据导入成功");

    ErrorCode(int i, String s) {
        this.code = i;
        this.message = s;
    }

    private int code;

    private String message;

}

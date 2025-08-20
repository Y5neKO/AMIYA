package com.y5neko.amiya.exception;

import lombok.Getter;

/**
 * 通用业务异常
 */
@Getter
public class BizException extends RuntimeException {
    private final int code;

    public BizException(String message) {
        super(message);
        this.code = 400;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

}

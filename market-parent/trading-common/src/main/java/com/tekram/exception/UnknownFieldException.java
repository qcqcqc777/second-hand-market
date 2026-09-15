package com.tekram.exception;

/**
 * 请求包含接口未定义的字段。
 */
public class UnknownFieldException extends RuntimeException {
    public UnknownFieldException(String fieldName) {
        super("Unknown field: " + fieldName);
    }
}

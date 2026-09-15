package com.tekram.exception;

/** 请求超过接口限流阈值。 */
public class RateLimitedException extends RuntimeException {
    public RateLimitedException() {
        super("Request rate limited");
    }
}

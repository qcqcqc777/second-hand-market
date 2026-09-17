package com.tekram.handler;

import com.tekram.response.Result;
import com.tekram.exception.RateLimitedException;
import com.tekram.exception.UnknownFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** 全局异常处理器，统一将 Spring MVC 异常转换为项目的 Result 响应格式。 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 请求体缺失、格式错误或无法反序列化。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> invalidBody(HttpMessageNotReadableException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof UnknownFieldException) {
                return ResponseEntity.badRequest().body(Result.error("UNKNOWN_FIELD"));
            }
        }
        return ResponseEntity.badRequest().body(Result.error("VALIDATION_ERROR"));
    }

    /** 使用 Spring 自带异常返回业务错误，无需为每个模块创建异常类。 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Result> businessError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(Result.error(exception.getReason()));
    }

    /** @RequestParam 等请求参数缺失。 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result> missingParameter(MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest().body(Result.error("VALIDATION_ERROR"));
    }

    /** @Valid/@Validated 参数校验失败。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> invalidParameter(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(Result.error("VALIDATION_ERROR"));
    }

    /** 请求包含接口未定义的字段。 */
    @ExceptionHandler(UnknownFieldException.class)
    public ResponseEntity<Result> unknownField(UnknownFieldException exception) {
        return ResponseEntity.badRequest().body(Result.error("UNKNOWN_FIELD"));
    }

    /** 请求超过限流阈值。 */
    @ExceptionHandler(RateLimitedException.class)
    public ResponseEntity<Result> rateLimited(RateLimitedException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Result.error("RATE_LIMITED"));
    }

    /** 未预期异常统一记录日志，并避免向客户端暴露堆栈信息。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> internalError(Exception exception) {
        log.error("Unhandled request exception", exception);
        return ResponseEntity.internalServerError().body(Result.error("INTERNAL_ERROR"));
    }
}

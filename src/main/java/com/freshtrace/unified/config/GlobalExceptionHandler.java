package com.freshtrace.unified.config;

import com.freshtrace.unified.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 记录完整堆栈
        e.printStackTrace();
        // 业务异常（RuntimeException）返回错误信息，系统异常返回通用消息
        if (e instanceof RuntimeException) {
            return Result.error(e.getMessage());
        }
        return Result.error("系统繁忙，请稍后重试");
    }
}

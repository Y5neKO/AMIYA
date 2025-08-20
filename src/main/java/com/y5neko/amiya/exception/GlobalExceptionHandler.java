package com.y5neko.amiya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 全局异常处理类
 * 处理应用程序中抛出的所有异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 自定义异常返回
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("type", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
        response.put("type", "ValidationException");
        return ResponseEntity.badRequest().body(response);
    }

    // Spring Security 403 异常
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "无权限访问");
        response.put("type", "AccessDenied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 自定义业务异常返回
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Map<String, Object>> handleBizException(BizException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());
        body.put("code", ex.getCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

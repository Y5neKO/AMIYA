package com.y5neko.amiya.exception;

import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.util.LogUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理类
 * 处理应用程序中抛出的所有异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public ApiResponse<?> handleBizException(BizException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    /**
     * 处理 Spring Security 403 异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "无权限访问");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 处理 JSON 格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleJsonParseException(HttpMessageNotReadableException ex) {
        return ApiResponse.error("请求数据格式错误");
    }

    /**
     * 处理 @Valid / @RequestBody 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        response.put("errors", errors);
        response.put("code", HttpStatus.BAD_REQUEST.value());
        return response;
    }

    /**
     * 处理 @Validated(groups=...) 校验失败（通常是 @RequestParam、@PathVariable、@ModelAttribute 等）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);

        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        }

        response.put("errors", errors);
        response.put("code", HttpStatus.BAD_REQUEST.value());
        return response;
    }

    /**
     * 处理其它未知异常
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleOtherException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "服务器内部错误");
        response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        LogUtils.error("未知异常：" + ex.getMessage(), ex);
        return response;
    }
}

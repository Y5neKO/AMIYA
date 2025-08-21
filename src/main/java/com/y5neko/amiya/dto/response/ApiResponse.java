package com.y5neko.amiya.dto.response;

import lombok.Data;

/**
 * API 响应类
 * 用于统一 API 响应格式
 */
@Data
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(true);
        resp.setMessage("成功");
        resp.setData(data);
        return resp;
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String msg) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setSuccess(false);
        resp.setMessage(msg);
        resp.setData(null);
        return resp;
    }
}

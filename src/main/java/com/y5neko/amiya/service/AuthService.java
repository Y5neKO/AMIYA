package com.y5neko.amiya.service;

import com.y5neko.amiya.dto.request.AuthRequest;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(AuthRequest request);

    Long getUserIdByUsername(String username);

    void changePassword(Long userId, String oldPassword, String newPassword);
}

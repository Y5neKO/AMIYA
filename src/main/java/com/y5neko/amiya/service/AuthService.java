package com.y5neko.amiya.service;

import com.y5neko.amiya.dto.AuthRequest;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(AuthRequest request);
}

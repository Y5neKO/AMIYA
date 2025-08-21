package com.y5neko.amiya.controller;

import com.y5neko.amiya.dto.request.AuthRequest;
import com.y5neko.amiya.dto.request.PasswordChangeRequest;
import com.y5neko.amiya.security.util.JwtUtils;
import com.y5neko.amiya.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供登录功能
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证接口", description = "认证相关操作")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码登录")
    public Map<String, Object> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "通过原密码修改密码")
    public Map<String, Object> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                              HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未提供JWT");
        }
        token = token.substring(7);

        JwtUtils.UserRole userRole = JwtUtils.parseUserRole(token); // 解析JWT获取当前用户
        Long userId = authService.getUserIdByUsername(userRole.getUsername());

        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "密码修改成功");
        return resp;
    }
}

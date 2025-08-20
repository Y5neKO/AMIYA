package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.y5neko.amiya.entity.Role;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.mapper.RoleMapper;
import com.y5neko.amiya.mapper.UserMapper;
import com.y5neko.amiya.security.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Autowired
    public AuthController(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        Map<String, Object> response = new HashMap<>();

        // 检查用户是否存在
        if (user == null) {
            response.put("success", false);
            response.put("message", "账号或密码错误");
            return response;
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            response.put("success", false);
            response.put("message", "账号或密码错误");
            return response;
        }

        // 根据 roleId 查找角色
        Role role = roleMapper.selectById(user.getRoleId());
        String roleName = (role != null) ? role.getRoleName() : "user";

        // 构建JWT载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", roleName);
        claims.put("email", user.getEmail());
        claims.put("isActive", user.getIsActive());
        // LocalDateTime 转字符串
        claims.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        claims.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);

        // 生成JWT，有效期1小时
        String token = JwtUtils.generateToken(claims, 3600_000);

        response.put("success", true);
        response.put("token", token);
        return response;
    }

    /**
     * 刷新 JWT
     * @param authHeader 旧 token
     * @return 新 token
     */
    @PostMapping("/refresh")
    public Map<String, Object> refreshToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("success", false);
            response.put("message", "缺少或错误的 Authorization 头");
            return response;
        }

        String oldToken = authHeader.substring(7);

        try {
            // 解析旧 token，不验证过期时间，以便提前刷新
            Map<String, Object> claims = JwtUtils.parseToken(oldToken);

            // 生成新 token（1小时有效期）
            String newToken = JwtUtils.generateToken(claims, 3600_000);

            response.put("success", true);
            response.put("token", newToken);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "无效的 token");
        }

        return response;
    }
}

package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.y5neko.amiya.dto.request.AuthRequest;
import com.y5neko.amiya.entity.Role;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.mapper.RoleMapper;
import com.y5neko.amiya.mapper.UserMapper;
import com.y5neko.amiya.security.util.JwtUtils;
import com.y5neko.amiya.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Autowired
    public AuthServiceImpl(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public Map<String, Object> login(AuthRequest request) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 校验账号密码
        Map<String, Object> response = new HashMap<>();
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            response.put("success", false);
            response.put("message", "账号或密码错误");
            return response;
        }

        // 校验账户是否启用
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BizException("账户已被禁用，请联系管理员");
        }

        // 获取角色信息
        Role role = roleMapper.selectById(user.getRoleId());
        String roleName = (role != null) ? role.getRoleName() : "user";

        // 生成JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", roleName);
        claims.put("email", user.getEmail());
        claims.put("isActive", user.getIsActive());
        claims.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        claims.put("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        // 设置过期时间
        String token = JwtUtils.generateToken(claims, 3600_000);

        // 响应
        response.put("success", true);
        response.put("data", claims);
        response.put("token", token);
        return response;
    }

    @Override
    public Long getUserIdByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user.getId();
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BizException("旧密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userMapper.updateById(user);
    }
}

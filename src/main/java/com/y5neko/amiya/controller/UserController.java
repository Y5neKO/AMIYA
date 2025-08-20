package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.ApiResponse;
import com.y5neko.amiya.dto.PageResponse;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.service.UserService;
import com.y5neko.amiya.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户相关的 HTTP 请求
 */
@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return ApiResponse.ok(user);
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<User>> listUsers(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        Page<User> pageData = userService.getPage(page, size, keyword);
        PageResponse<User> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );
        return ApiResponse.ok(resp);
    }

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BizException("用户名不能为空");
        }

        // 校验用户名唯一
        if (userService.getByUsername(user.getUsername()) != null) {
            throw new BizException("用户名已存在");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BizException("密码不能为空");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BizException("邮箱不能为空");
        }
        if (user.getRoleId() == null) {
            throw new BizException("角色ID不能为空");
        }

        // 校验角色是否存在
        if (userService.getRoleById(user.getRoleId()) == null) {
            throw new BizException("角色不存在");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return ApiResponse.ok(userService.create(user));
    }


    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User exist = userService.getById(id);
        if (exist == null) {
            throw new BizException("用户不存在");
        }

        // 校验用户名是否被其他用户占用
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            User userWithSameUsername = userService.getByUsername(user.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.getId().equals(id)) {
                throw new BizException("用户名已存在");
            }
        } else {
            throw new BizException("用户名不能为空");
        }

        // 校验其他必填字段
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BizException("邮箱不能为空");
        }
        if (user.getRoleId() == null) {
            throw new BizException("角色ID不能为空");
        }

        // 校验角色是否存在
        if (userService.getRoleById(user.getRoleId()) == null) {
            throw new BizException("角色不存在");
        }

        user.setId(id);

        // 如果传了密码，则加密；否则保留原密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        } else {
            user.setPassword(exist.getPassword());
        }

        return ApiResponse.ok(userService.update(user));
    }



    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        userService.delete(id);
        return ApiResponse.ok(null);
    }
}

package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.dto.request.UserRequest;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.service.RoleService;
import com.y5neko.amiya.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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

    @PostMapping("/create")
    public ApiResponse<User> createUser(
            @Validated(UserRequest.Create.class) @RequestBody UserRequest request) {

        if (userService.getByUsername(request.getUsername()) != null) {
            throw new BizException("用户名已存在");
        }
        if (roleService.getById(request.getRoleId()) == null) {
            throw new BizException("角色不存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setEmail(request.getEmail());
        user.setRoleId(request.getRoleId());
        user.setIsActive(request.getIsActive());

        return ApiResponse.ok(userService.create(user));
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(
            @PathVariable Long id,
            @Validated(UserRequest.Update.class) @RequestBody UserRequest request) {

        User exist = userService.getById(id);
        if (exist == null) {
            throw new BizException("用户不存在");
        }

        if (userService.getByUsername(request.getUsername()) != null &&
                !exist.getUsername().equals(request.getUsername())) {
            throw new BizException("用户名已存在");
        }
        if (roleService.getById(request.getRoleId()) == null) {
            throw new BizException("角色不存在");
        }

        exist.setUsername(request.getUsername());
        exist.setEmail(request.getEmail());
        exist.setRoleId(request.getRoleId());
        exist.setIsActive(request.getIsActive());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            exist.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        return ApiResponse.ok(userService.update(exist));
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

package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.dto.request.UserRequest;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.service.RoleService;
import com.y5neko.amiya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器<br><br>
 * <b>只有管理员才能管理用户</b>
 */
@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户", description = "根据用户ID获取用户信息")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return ApiResponse.ok(user);
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "根据分页参数和可选的关键词获取用户列表")
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
    @Operation(summary = "创建用户", description = "根据用户请求创建用户")
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
    @Operation(summary = "更新用户", description = "根据用户ID和用户请求更新用户信息")
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

    /**
     * 删除用户(必须保留一个默认管理员)
     * @param id 用户ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        // 判断删除的是不是默认管理员
        if (userService.getById(id).getRoleId().equals(1L)) {
            throw new BizException("不能删除默认管理员");
        }

        userService.delete(id);
        return ApiResponse.ok(null);
    }
}

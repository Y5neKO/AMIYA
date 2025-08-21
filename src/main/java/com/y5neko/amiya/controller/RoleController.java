package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.dto.RoleRequest;
import com.y5neko.amiya.entity.Role;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.mapper.UserMapper;
import com.y5neko.amiya.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;
    private final UserMapper userMapper;

    public RoleController(RoleService roleService, UserMapper userMapper) {
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ApiResponse<Role> getRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BizException("角色不存在");
        }
        return ApiResponse.ok(role);
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Role>> listRoles(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Role> pageData = roleService.getPage(page, size, keyword);
        PageResponse<Role> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );
        return ApiResponse.ok(resp);
    }

    @PostMapping
    public ApiResponse<Role> createRole(
            @Validated(RoleRequest.Create.class) @RequestBody RoleRequest request) {

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        return ApiResponse.ok(roleService.create(role));
    }

    @PutMapping("/{id}")
    public ApiResponse<Role> updateRole(
            @PathVariable Long id,
            @Validated(RoleRequest.Update.class) @RequestBody RoleRequest request) {

        Role exist = roleService.getById(id);
        if (exist == null) {
            throw new BizException("角色不存在");
        }
        exist.setRoleName(request.getRoleName());
        return ApiResponse.ok(roleService.update(exist));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BizException("角色不存在");
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        List<User> users = userMapper.selectList(wrapper);
        if (!users.isEmpty()) {
            throw new BizException("该角色下存在用户，无法删除");
        }

        roleService.delete(id);
        return ApiResponse.ok(null);
    }
}

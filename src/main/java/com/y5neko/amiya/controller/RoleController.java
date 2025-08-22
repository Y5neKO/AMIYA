package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.dto.request.RoleRequest;
import com.y5neko.amiya.entity.Role;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.mapper.UserMapper;
import com.y5neko.amiya.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器<br><br>
 * <b>只有管理员才能管理角色</b>
 */
@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "角色接口", description = "角色相关操作")
public class RoleController {

    private final RoleService roleService;
    private final UserMapper userMapper;

    public RoleController(RoleService roleService, UserMapper userMapper) {
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取角色", description = "根据角色ID获取角色信息")
    public ApiResponse<Role> getRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BizException("角色不存在");
        }
        return ApiResponse.ok(role);
    }

    @GetMapping("/list")
    @Operation(summary = "获取角色列表", description = "根据分页参数和可选关键字获取角色列表")
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

    @PostMapping("/create")
    @Operation(summary = "创建角色", description = "根据角色请求创建角色")
    public ApiResponse<Role> createRole(
            @Validated(RoleRequest.Create.class) @RequestBody RoleRequest request) {

        Role role = new Role();
        role.setRoleName(request.getRoleName());

        roleService.create(role);
        Role createdRole = roleService.getById(role.getId());
        return ApiResponse.ok(createdRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "根据角色ID和请求更新角色信息")
    public ApiResponse<Role> updateRole(
            @PathVariable Long id,
            @Validated(RoleRequest.Update.class) @RequestBody RoleRequest request) {

        Role exist = roleService.getById(id);
        if (exist == null) {
            throw new BizException("角色不存在");
        }

        // 判断修改的是不是默认管理员角色
        if (id.equals(1L)) {
            throw new BizException("不能修改默认管理员角色");
        }

        exist.setRoleName(request.getRoleName());
        roleService.update(exist);
        Role updatedRole = roleService.getById(id);
        return ApiResponse.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BizException("角色不存在");
        }

        // 判断删除的是不是默认管理员角色
        if (id.equals(1L)) {
            throw new BizException("不能删除默认管理员角色");
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

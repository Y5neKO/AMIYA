package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.PageResponse;
import com.y5neko.amiya.entity.Role;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.mapper.UserMapper;
import com.y5neko.amiya.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Role getRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BizException("角色不存在");
        }
        return role;
    }

    @GetMapping("/list")
    public PageResponse<Role> listRoles(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Role> pageData = roleService.getPage(page, size, keyword);
        return new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );
    }

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            throw new BizException("角色名不能为空");
        }
        return roleService.create(role);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role exist = roleService.getById(id);
        if (exist == null) {
            throw new BizException("角色不存在");
        }
        role.setId(id);
        return roleService.update(role);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BizException("角色不存在");
        }

        // 查询是否有用户绑定该角色
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        List<User> users = userMapper.selectList(wrapper);
        if (!users.isEmpty()) {
            throw new BizException("该角色下存在用户，无法删除");
        }

        roleService.delete(id);
    }
}

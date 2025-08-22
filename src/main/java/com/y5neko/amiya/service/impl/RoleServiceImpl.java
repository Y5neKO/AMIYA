package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Role;
import com.y5neko.amiya.mapper.RoleMapper;
import com.y5neko.amiya.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role create(Role role) {
        roleMapper.insert(role);
        return role;
    }

    @Override
    public Role update(Role role) {
        roleMapper.updateById(role);
        return role;
    }

    @Override
    public void delete(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public Page<Role> getPage(long page, long size, String keyword) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("role_name", keyword)
                    .or()
                    .like("description", keyword);
        }
        return roleMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Role getByName(String roleName) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name", roleName);
        return roleMapper.selectOne(wrapper);
    }
}

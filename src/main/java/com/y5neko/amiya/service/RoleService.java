package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Role;

public interface RoleService {

    Role getById(Long id);

    Role create(Role role);

    Role update(Role role);

    void delete(Long id);

    Page<Role> getPage(long page, long size, String keyword);
}

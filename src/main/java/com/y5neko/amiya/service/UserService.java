package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.User;
import jakarta.validation.constraints.NotBlank;

public interface UserService {

    User getById(Long id);

    User create(User user);

    User update(User user);

    void delete(Long id);

    /**
     * 分页查询用户
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字，匹配用户名或邮箱
     * @return 分页用户数据
     */
    Page<User> getPage(long page, long size, String keyword);

    User getByUsername(@NotBlank(message = "用户名不能为空") String username);
}

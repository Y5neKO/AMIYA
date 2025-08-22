package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.TaskResult;

public interface TaskResultService {

    TaskResult getById(Long id);

    void delete(Long id);

    /**
     * 分页查询任务结果（带权限过滤）
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字
     * @param userId 普通用户ID，null 表示管理员可查看所有
     * @return 分页结果
     */
    Page<TaskResult> getPage(long page, long size, String keyword, Long userId);
}

package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Task;

public interface TaskService {

    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务
     */
    Task getById(Long id);

    /**
     * 创建任务
     * @param task 任务实体
     * @return 创建后的任务
     */
    Task create(Task task);

    /**
     * 更新任务
     * @param task 任务实体
     * @return 更新后的任务
     */
    Task update(Task task);

    /**
     * 删除任务
     * @param id 任务ID
     */
    void delete(Long id);

    /**
     * 分页查询任务
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字
     * @return 分页结果
     */
    Page<Task> getPage(long page, long size, String keyword);

    /**
     * 分页查询任务（带用户权限过滤）
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字
     * @param createdBy 普通用户ID，null表示管理员可查看所有
     * @return 分页结果
     */
    Page<Task> getPage(long page, long size, String keyword, Long createdBy);
}

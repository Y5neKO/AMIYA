package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Task;
import com.y5neko.amiya.mapper.TaskMapper;
import com.y5neko.amiya.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public Task getById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public Task create(Task task) {
        taskMapper.insert(task);
        return task;
    }

    @Override
    public Task update(Task task) {
        taskMapper.updateById(task);
        return task;
    }

    @Override
    public void delete(Long id) {
        taskMapper.deleteById(id);
    }

    /**
     * 分页查询任务
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字（任务名）
     * @return 分页结果
     */
    @Override
    public Page<Task> getPage(long page, long size, String keyword) {
        return getPage(page, size, keyword, null);
    }

    /**
     * 分页查询任务（带用户权限过滤）
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字
     * @param createdBy 普通用户ID，null表示管理员可查看所有
     * @return 分页结果
     */
    public Page<Task> getPage(long page, long size, String keyword, Long createdBy) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("name", keyword);
        }
        if (createdBy != null) {
            wrapper.eq("created_by", createdBy);
        }
        return taskMapper.selectPage(new Page<>(page, size), wrapper);
    }
}

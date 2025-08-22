package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Task;
import com.y5neko.amiya.entity.TaskResult;
import com.y5neko.amiya.mapper.TaskResultMapper;
import com.y5neko.amiya.service.TaskResultService;
import com.y5neko.amiya.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskResultServiceImpl implements TaskResultService {

    private final TaskResultMapper taskResultMapper;
    private final TaskService taskService;

    public TaskResultServiceImpl(TaskResultMapper taskResultMapper, TaskService taskService) {
        this.taskResultMapper = taskResultMapper;
        this.taskService = taskService;
    }

    @Override
    public TaskResult getById(Long id) {
        return taskResultMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        taskResultMapper.deleteById(id);
    }

    @Override
    public Page<TaskResult> getPage(long page, long size, String keyword, Long userId) {
        QueryWrapper<TaskResult> wrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("summary", keyword);
        }

        // 普通用户只能看到自己创建的任务的结果
        if (userId != null) {
            List<Task> userTasks = taskService.getPage(1, Integer.MAX_VALUE, null, userId).getRecords();
            if (userTasks.isEmpty()) {
                return new Page<>(page, size); // 没有任务，返回空分页
            }
            List<Long> taskIds = userTasks.stream().map(Task::getId).collect(Collectors.toList());
            wrapper.in("task_id", taskIds);
        }

        return taskResultMapper.selectPage(new Page<>(page, size), wrapper);
    }
}

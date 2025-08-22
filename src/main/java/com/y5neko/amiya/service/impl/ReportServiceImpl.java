package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Report;
import com.y5neko.amiya.entity.Task;
import com.y5neko.amiya.mapper.ReportMapper;
import com.y5neko.amiya.service.ReportService;
import com.y5neko.amiya.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final TaskService taskService;

    public ReportServiceImpl(ReportMapper reportMapper, TaskService taskService) {
        this.reportMapper = reportMapper;
        this.taskService = taskService;
    }

    @Override
    public Report getById(Long id) {
        return reportMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        reportMapper.deleteById(id);
    }

    @Override
    public Page<Report> getPage(long page, long size, Long userId) {
        QueryWrapper<Report> wrapper = new QueryWrapper<>();

        if (userId != null) {
            // 普通用户只看自己创建的任务的报告
            List<Task> userTasks = taskService.getPage(1, Integer.MAX_VALUE, null, userId).getRecords();
            if (userTasks.isEmpty()) {
                return new Page<>(page, size); // 没有任务
            }
            List<Long> taskIds = userTasks.stream().map(Task::getId).collect(Collectors.toList());
            wrapper.in("task_id", taskIds);
        }

        return reportMapper.selectPage(new Page<>(page, size), wrapper);
    }
}

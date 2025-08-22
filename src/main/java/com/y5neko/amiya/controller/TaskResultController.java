package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.entity.Task;
import com.y5neko.amiya.entity.TaskResult;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.security.util.JwtUtils;
import com.y5neko.amiya.service.TaskResultService;
import com.y5neko.amiya.service.TaskService;
import com.y5neko.amiya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task-result")
@Tag(name = "任务结果管理")
public class TaskResultController {

    private final TaskResultService taskResultService;
    private final TaskService taskService;
    private final UserService userService;

    public TaskResultController(TaskResultService taskResultService,
                                TaskService taskService,
                                UserService userService) {
        this.taskResultService = taskResultService;
        this.taskService = taskService;
        this.userService = userService;
    }

    private JwtUtils.UserRole getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BizException("未提供JWT");
        }
        token = token.substring(7);
        return JwtUtils.parseUserRole(token);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取任务结果详情")
    public ApiResponse<TaskResult> getResult(@PathVariable Long id, HttpServletRequest request) {
        TaskResult result = taskResultService.getById(id);
        if (result == null) throw new BizException("任务结果不存在");

        Task task = taskService.getById(result.getTaskId());
        if (task == null) throw new BizException("关联任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        if (!userRole.isAdmin() && !task.getCreatedBy().equals(currentUserId)) {
            throw new BizException("没有权限访问该任务结果");
        }

        return ApiResponse.ok(result);
    }

    @GetMapping("/list")
    @Operation(summary = "获取任务结果列表（分页）")
    public ApiResponse<PageResponse<TaskResult>> listResults(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = null;
        if (!userRole.isAdmin()) {
            currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        }

        Page<TaskResult> pageData = taskResultService.getPage(page, size, keyword, currentUserId);

        PageResponse<TaskResult> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );

        return ApiResponse.ok(resp);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务结果")
    public ApiResponse<Void> deleteResult(@PathVariable Long id, HttpServletRequest request) {
        TaskResult result = taskResultService.getById(id);
        if (result == null) throw new BizException("任务结果不存在");

        Task task = taskService.getById(result.getTaskId());
        if (task == null) throw new BizException("关联任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        if (!userRole.isAdmin() && !task.getCreatedBy().equals(currentUserId)) {
            throw new BizException("没有权限删除该任务结果");
        }

        taskResultService.delete(id);
        return ApiResponse.ok(null);
    }
}

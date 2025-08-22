package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.entity.Report;
import com.y5neko.amiya.entity.Task;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.security.util.JwtUtils;
import com.y5neko.amiya.service.ReportService;
import com.y5neko.amiya.service.TaskService;
import com.y5neko.amiya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Tag(name = "报告管理")
public class ReportController {

    private final ReportService reportService;
    private final TaskService taskService;
    private final UserService userService;

    public ReportController(ReportService reportService,
                            TaskService taskService,
                            UserService userService) {
        this.reportService = reportService;
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
    @Operation(summary = "获取报告详情")
    public ApiResponse<Report> getById(@PathVariable Long id, HttpServletRequest request) {
        Report report = reportService.getById(id);
        if (report == null) throw new BizException("报告不存在");

        Task task = taskService.getById(report.getTaskId());
        if (task == null) throw new BizException("关联任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();

        if (!userRole.isAdmin() && !task.getCreatedBy().equals(currentUserId)) {
            throw new BizException("没有权限查看该报告");
        }

        return ApiResponse.ok(report);
    }

    @GetMapping("/list")
    @Operation(summary = "获取报告列表（分页）")
    public ApiResponse<PageResponse<Report>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest request
    ) {
        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = null;
        if (!userRole.isAdmin()) {
            currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        }

        Page<Report> pageData = reportService.getPage(page, size, currentUserId);

        PageResponse<Report> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );
        return ApiResponse.ok(resp);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除报告")
    public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Report report = reportService.getById(id);
        if (report == null) throw new BizException("报告不存在");

        Task task = taskService.getById(report.getTaskId());
        if (task == null) throw new BizException("关联任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();

        if (!userRole.isAdmin() && !task.getCreatedBy().equals(currentUserId)) {
            throw new BizException("没有权限删除该报告");
        }

        reportService.delete(id);
        return ApiResponse.ok(null);
    }
}

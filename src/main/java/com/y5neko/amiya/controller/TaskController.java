package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.request.TaskRequest;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.entity.Task;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.security.util.JwtUtils;
import com.y5neko.amiya.service.AssetService;
import com.y5neko.amiya.service.TaskService;
import com.y5neko.amiya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/task")
@Tag(name = "任务管理")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final AssetService assetService;

    public TaskController(TaskService taskService, UserService userService, AssetService assetService) {
        this.taskService = taskService;
        this.userService = userService;
        this.assetService = assetService;
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
    @Operation(summary = "获取任务详情")
    public ApiResponse<Task> getTask(@PathVariable Long id, HttpServletRequest request) {
        Task task = taskService.getById(id);
        if (task == null) throw new BizException("任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        if (!userRole.isAdmin() && !task.getCreatedBy().equals(userService.getByUsername(userRole.getUsername()).getId())) {
            throw new BizException("没有权限访问该任务");
        }
        return ApiResponse.ok(task);
    }

    @GetMapping("/list")
    @Operation(summary = "获取任务列表（分页）")
    public ApiResponse<PageResponse<Task>> listTasks(
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

        Page<Task> pageData = taskService.getPage(page, size, keyword, currentUserId);
        PageResponse<Task> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );

        return ApiResponse.ok(resp);
    }

    @PostMapping("/create")
    @Operation(summary = "创建任务")
    public ApiResponse<Task> createTask(
            @Validated(TaskRequest.Create.class) @RequestBody TaskRequest req,
            HttpServletRequest request) {

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();

        Asset asset = assetService.getById(req.getAssetId());
        if (asset == null) throw new BizException("资产不存在");

        User assetOwner = userService.getById(asset.getOwnerId());
        if (assetOwner == null) throw new BizException("资产所有者不存在");

        if (!userRole.isAdmin() && !asset.getOwnerId().equals(currentUserId)) {
            throw new BizException("没有权限使用其他用户的资产");
        }

        // 仅在 cron 模式下校验 Cron 表达式
        if ("cron".equals(req.getScheduleType())) {
            if (req.getCronExpr() == null || req.getCronExpr().trim().isEmpty()) {
                throw new BizException("Cron 类型任务必须填写 cronExpr");
            }
            if (!Pattern.matches("^(\\*|([0-5]?\\d))(\\s+(\\*|([01]?\\d|2[0-3]))){4,5}$", req.getCronExpr())) {
                throw new BizException("Cron 表达式格式不正确");
            }
        }

        Task task = new Task();
        BeanUtils.copyProperties(req, task);
        task.setCreatedBy(currentUserId);
        task.setStatus("pending"); // 创建时状态固定为 pending
        if ("once".equals(req.getScheduleType())) {
            task.setCronExpr(null);
        }

        taskService.create(task);
        Task createdTask = taskService.getById(task.getId());
        return ApiResponse.ok(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新任务")
    public ApiResponse<Task> updateTask(
            @PathVariable Long id,
            @Validated(TaskRequest.Update.class) @RequestBody TaskRequest req,
            HttpServletRequest request) {

        Task exist = taskService.getById(id);
        if (exist == null) throw new BizException("任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        if (!userRole.isAdmin() && !exist.getCreatedBy().equals(currentUserId)) {
            throw new BizException("没有权限更新该任务");
        }

        Asset asset = assetService.getById(req.getAssetId());
        if (asset == null) throw new BizException("资产不存在");

        if (!userRole.isAdmin() && !asset.getOwnerId().equals(currentUserId)) {
            throw new BizException("没有权限将任务更新到其他用户的资产");
        }

        User assetOwner = userService.getById(asset.getOwnerId());
        if (assetOwner == null) throw new BizException("资产所有者不存在");

        // 仅在 cron 模式下校验 Cron 表达式
        if ("cron".equals(req.getScheduleType())) {
            if (req.getCronExpr() == null || req.getCronExpr().trim().isEmpty()) {
                throw new BizException("Cron 类型任务必须填写 cronExpr");
            }
            if (!Pattern.matches("^(\\*|([0-5]?\\d))(\\s+(\\*|([01]?\\d|2[0-3]))){4,5}$", req.getCronExpr())) {
                throw new BizException("Cron 表达式格式不正确");
            }
        }

        // 更新任务，保留 createdBy 和 status，不允许用户修改
        Task task = new Task();
        BeanUtils.copyProperties(req, task);
        task.setId(id);
        task.setCreatedBy(exist.getCreatedBy());
        task.setStatus(exist.getStatus());
        if ("once".equals(req.getScheduleType())) {
            task.setCronExpr(null);
        }

        taskService.update(task);
        Task updatedTask = taskService.getById(id); // 重新查询完整对象
        return ApiResponse.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务")
    public ApiResponse<Void> deleteTask(@PathVariable Long id, HttpServletRequest request) {
        Task task = taskService.getById(id);
        if (task == null) throw new BizException("任务不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        if (!userRole.isAdmin() && !task.getCreatedBy().equals(currentUserId)) {
            throw new BizException("没有权限删除该任务");
        }

        taskService.delete(id);
        return ApiResponse.ok(null);
    }
}

package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.request.AssetRequest;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.entity.User;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.security.util.JwtUtils;
import com.y5neko.amiya.service.AssetService;
import com.y5neko.amiya.service.UserService;
import com.y5neko.amiya.util.MiscUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产控制器
 * 管理员可以管理所有资产，普通用户只能管理自己的资产
 */
@RestController
@RequestMapping("/asset")
@Tag(name = "资产接口", description = "资产相关操作")
public class AssetController {

    private final AssetService assetService;
    private final UserService userService;

    public AssetController(AssetService assetService, UserService userService) {
        this.assetService = assetService;
        this.userService = userService;
    }

    /**
     * 获取当前用户角色
     * @param request HTTP请求
     * @return 用户角色
     */
    private JwtUtils.UserRole getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BizException("未提供JWT");
        }
        token = token.substring(7);
        return JwtUtils.parseUserRole(token);
    }

    /**
     * 获取资产详情
     * @param id 资产ID
     * @param request HTTP请求
     * @return 资产详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取资产信息", description = "根据ID获取资产详细信息")
    public ApiResponse<Asset> getAsset(@PathVariable Long id, HttpServletRequest request) {
        Asset asset = assetService.getById(id);
        if (asset == null) throw new BizException("资产不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        if (!userRole.isAdmin() && !asset.getOwnerId().equals(userService.getByUsername(userRole.getUsername()).getId())) {
            throw new BizException("没有权限访问该资产");
        }

        return ApiResponse.ok(asset);
    }

    /**
     * 获取资产列表
     * @param page 当前页
     * @param size 每页条数
     * @param keyword 可选关键字，匹配资产名称或标签
     * @param request HTTP请求
     * @return 资产列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取资产列表", description = "根据分页参数和可选关键字获取资产列表")
    public ApiResponse<PageResponse<Asset>> listAssets(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        // 管理员可以查看所有资产，普通用户只能查看自己的资产
        JwtUtils.UserRole userRole = getCurrentUser(request);
        Long currentUserId = null;
        if (!userRole.isAdmin()) {
            currentUserId = userService.getByUsername(userRole.getUsername()).getId();
        }
        // currentUserId为null时说明是管理员
        Page<Asset> pageData = assetService.getPage(page, size, keyword, currentUserId);

        PageResponse<Asset> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );

        return ApiResponse.ok(resp);
    }

    /**
     * 创建资产
     * @param req 资产请求
     * @param request HTTP请求
     * @return 资产
     */
    @PostMapping("/create")
    @Operation(summary = "创建资产", description = "根据资产请求创建资产")
    public ApiResponse<Asset> createAsset(@Validated(AssetRequest.Create.class) @RequestBody AssetRequest req,
                                          HttpServletRequest request) {
        JwtUtils.UserRole userRole = getCurrentUser(request);
        if (!userRole.isAdmin() && !req.getOwnerId().equals(userService.getByUsername(userRole.getUsername()).getId())) {
            throw new BizException("没有权限为其他用户创建资产");
        }

        // 名称唯一性校验
        Asset existAsset = assetService.getByName(req.getName());
        if (existAsset != null) {
            throw new BizException("资产名称已存在");
        }

        // 校验所属用户是否存在
        User owner = userService.getById(req.getOwnerId());
        if (owner == null) throw new BizException("资产所有者不存在");

        Asset asset = new Asset();
        BeanUtils.copyProperties(req, asset);
        asset.setTags(MiscUtils.convertListToArray(req.getTags()));
        return ApiResponse.ok(assetService.create(asset));
    }

    /**
     * 更新资产
     * @param id 资产ID
     * @param req 资产请求
     * @param request HTTP请求
     * @return 资产
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新资产", description = "根据资产ID和资产请求更新资产")
    public ApiResponse<Asset> updateAsset(@PathVariable Long id,
                                          @Validated(AssetRequest.Update.class) @RequestBody AssetRequest req,
                                          HttpServletRequest request) {
        Asset exist = assetService.getById(id);
        if (exist == null) throw new BizException("资产不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        if (!userRole.isAdmin() && !exist.getOwnerId().equals(userService.getByUsername(userRole.getUsername()).getId())) {
            throw new BizException("没有权限更新该资产");
        }

        User owner = userService.getById(req.getOwnerId());
        if (owner == null) throw new BizException("资产所有者不存在");

        BeanUtils.copyProperties(req, exist);
        exist.setId(id);
        exist.setTags(MiscUtils.convertListToArray(req.getTags()));
        return ApiResponse.ok(assetService.update(exist));
    }

    /**
     * 删除资产
     * @param id 资产ID
     * @param request HTTP请求
     * @return 无
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资产", description = "根据资产ID删除资产")
    public ApiResponse<Void> deleteAsset(@PathVariable Long id, HttpServletRequest request) {
        Asset asset = assetService.getById(id);
        if (asset == null) throw new BizException("资产不存在");

        JwtUtils.UserRole userRole = getCurrentUser(request);
        if (!userRole.isAdmin() && !asset.getOwnerId().equals(userService.getByUsername(userRole.getUsername()).getId())) {
            throw new BizException("没有权限删除该资产");
        }

        assetService.delete(id);
        return ApiResponse.ok(null);
    }
}

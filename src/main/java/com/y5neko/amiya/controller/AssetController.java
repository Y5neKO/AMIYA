package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.response.ApiResponse;
import com.y5neko.amiya.dto.AssetRequest;
import com.y5neko.amiya.dto.response.PageResponse;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.service.AssetService;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/{id}")
    public ApiResponse<Asset> getAsset(@PathVariable Long id) {
        Asset asset = assetService.getById(id);
        if (asset == null) {
            throw new BizException("资产不存在");
        }
        return ApiResponse.ok(asset);
    }

    @GetMapping("/list")
    public ApiResponse<PageResponse<Asset>> listAssets(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Asset> pageData = (keyword == null || keyword.trim().isEmpty())
                ? assetService.getPage(page, size, null)
                : assetService.getPage(page, size, keyword);

        PageResponse<Asset> resp = new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );

        return ApiResponse.ok(resp);
    }

    @PostMapping
    public ApiResponse<Asset> createAsset(@Validated(AssetRequest.Create.class) @RequestBody AssetRequest req) {
        Asset asset = new Asset();
        BeanUtils.copyProperties(req, asset);
        return ApiResponse.ok(assetService.create(asset));
    }

    @PutMapping("/{id}")
    public ApiResponse<Asset> updateAsset(@PathVariable Long id,
                                          @Validated(AssetRequest.Update.class) @RequestBody AssetRequest req) {
        Asset exist = assetService.getById(id);
        if (exist == null) {
            throw new BizException("资产不存在");
        }
        BeanUtils.copyProperties(req, exist);
        exist.setId(id);
        return ApiResponse.ok(assetService.update(exist));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAsset(@PathVariable Long id) {
        Asset asset = assetService.getById(id);
        if (asset == null) {
            throw new BizException("资产不存在");
        }
        assetService.delete(id);
        return ApiResponse.ok(null);
    }
}

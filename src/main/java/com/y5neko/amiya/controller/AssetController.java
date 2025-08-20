package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.dto.PageResponse;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.exception.BizException;
import com.y5neko.amiya.service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/{id}")
    public Asset getAsset(@PathVariable Long id) {
        Asset asset = assetService.getById(id);
        if (asset == null) {
            throw new BizException("资产不存在");
        }
        return asset;
    }

    @GetMapping("/list")
    public PageResponse<Asset> listAssets(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        Page<Asset> pageData;
        if (keyword == null || keyword.trim().isEmpty()) {
            pageData = assetService.getPage(page, size, null);
        } else {
            pageData = assetService.getPage(page, size, keyword);
        }

        return new PageResponse<>(
                pageData.getCurrent(),
                pageData.getSize(),
                pageData.getTotal(),
                pageData.getRecords()
        );
    }

    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            throw new BizException("资产名称不能为空");
        }
        return assetService.create(asset);
    }

    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        Asset exist = assetService.getById(id);
        if (exist == null) {
            throw new BizException("资产不存在");
        }
        asset.setId(id);
        return assetService.update(asset);
    }

    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        Asset asset = assetService.getById(id);
        if (asset == null) {
            throw new BizException("资产不存在");
        }
        assetService.delete(id);
    }
}

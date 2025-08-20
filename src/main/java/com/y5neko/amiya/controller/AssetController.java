package com.y5neko.amiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.service.AssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    /**
     * 构造函数
     * @param assetService 资产服务
     */
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * 获取资产详情
     * @param id 资产ID
     * @return 资产详情
     */
    @GetMapping("/{id}")
    public Asset getAsset(@PathVariable Long id) {
        return assetService.getById(id);
    }

    /**
     * 分页获取资产列表
     * @param page 当前页，默认 1
     * @param size 每页条数，默认 10
     * @return 分页资产数据
     */
    @GetMapping("/list")
    public Page<Asset> getAssetsPage(@RequestParam(defaultValue = "1") long page,
                                     @RequestParam(defaultValue = "10") long size) {
        System.out.println("page = " + page);
        System.out.println("size = " + size);
        return assetService.getPage(page, size);
    }

    /**
     * 创建资产
     * @param asset 资产信息
     * @return 创建的资产
     */
    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return assetService.create(asset);
    }

    /**
     * 更新资产
     * @param id 资产ID
     * @param asset 资产信息
     * @return 更新的资产
     */
    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        asset.setId(id);
        return assetService.update(asset);
    }

    /**
     * 删除资产
     * @param id 资产ID
     */
    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
    }
}

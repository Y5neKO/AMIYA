package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.mapper.AssetMapper;
import com.y5neko.amiya.service.AssetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetMapper assetMapper;

    public AssetServiceImpl(AssetMapper assetMapper) {
        this.assetMapper = assetMapper;
    }

    @Override
    public Asset getById(Long id) {
        return assetMapper.selectById(id);
    }

    @Override
    public List<Asset> getAll() {
        return assetMapper.selectList(null);
    }

    @Override
    public Asset create(Asset asset) {
        assetMapper.insert(asset);
        return asset;
    }

    @Override
    public Asset update(Asset asset) {
        assetMapper.updateById(asset);
        return asset;
    }

    @Override
    public void delete(Long id) {
        assetMapper.deleteById(id);
    }

    /**
     * 分页获取资产列表
     * @param page 当前页，默认 1
     * @param size 每页条数，默认 10
     * @return 分页资产数据
     */
    @Override
    public Page<Asset> getPage(long page, long size) {
        return assetMapper.selectPage(new Page<>(page, size), new QueryWrapper<>());
    }
}

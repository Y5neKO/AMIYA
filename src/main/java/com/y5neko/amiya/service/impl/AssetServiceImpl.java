package com.y5neko.amiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.mapper.AssetMapper;
import com.y5neko.amiya.service.AssetService;
import org.springframework.stereotype.Service;

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

    @Override
    public Page<Asset> getPage(long page, long size, String keyword) {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword)
                    .or()
                    .like("ip", keyword)
                    .or()
                    .like("domain", keyword);
        }
        return assetMapper.selectPage(new Page<>(page, size), wrapper);
    }
}

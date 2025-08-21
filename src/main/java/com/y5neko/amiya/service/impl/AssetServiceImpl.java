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
        return getPage(page, size, keyword, null); // 默认管理员可以看到所有
    }

    // 新增一个私有方法，带 ownerId 权限过滤
    public Page<Asset> getPage(long page, long size, String keyword, Long ownerId) {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword)
                    .or()
                    .like("ip", keyword)
                    .or()
                    .like("domain", keyword);
        }
        if (ownerId != null) {
            wrapper.eq("owner_id", ownerId); // 普通用户只能看到自己的资产
        }
        return assetMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 根据名称查询资产
     * @param name 资产名称
     * @return 资产
     */
    @Override
    public Asset getByName(String name) {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        // 限制只返回一个结果
        wrapper.last("limit 1");
        return assetMapper.selectOne(wrapper);
    }
}

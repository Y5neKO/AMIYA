package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Asset;

public interface AssetService {

    Asset getById(Long id);

    Asset create(Asset asset);

    Asset update(Asset asset);

    void delete(Long id);

    Page<Asset> getPage(long page, long size, String keyword);

    Page<Asset> getPage(long page, long size, String keyword, Long ownerId);

    Asset getByName(String name);
}

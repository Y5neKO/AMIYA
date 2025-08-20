package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Asset;

import java.util.List;

public interface AssetService {

    Asset getById(Long id);

    List<Asset> getAll();

    Asset create(Asset asset);

    Asset update(Asset asset);

    void delete(Long id);

    Page<Asset> getPage(long page, long size);
}

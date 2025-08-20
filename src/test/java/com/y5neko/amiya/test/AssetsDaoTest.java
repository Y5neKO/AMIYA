package com.y5neko.amiya.test;

import com.y5neko.amiya.entity.Asset;
import com.y5neko.amiya.mapper.AssetMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class AssetsDaoTest {

    @Autowired
    private AssetMapper assetsMapper;

    @Test
    void testCRUD() {
        System.out.println("=========================testCRUD========================");

        // 插入
        Asset asset = new Asset();
        asset.setName("测试资产SpringBoot");
        asset.setIp("10.0.0.1");
        asset.setDomain("springboot.local");
        asset.setPort(8080);
        asset.setProtocol("tcp");
        asset.setTags(new String[]{"dev","api"});
        asset.setOwnerId(1L);
        asset.setStatus("active");

        assetsMapper.insert(asset);

        // 查询
        Asset dbAsset = assetsMapper.selectById(asset.getId());
        System.out.println("查询结果源数据: " + dbAsset);
        System.out.println("查询结果: " + dbAsset.getName() + ", tags: " + Arrays.toString(dbAsset.getTags()));

        // 更新
        dbAsset.setStatus("inactive");
        assetsMapper.updateById(dbAsset);

        // 删除
        assetsMapper.deleteById(dbAsset.getId());

        System.out.println("=========================testCRUD========================");
    }
}

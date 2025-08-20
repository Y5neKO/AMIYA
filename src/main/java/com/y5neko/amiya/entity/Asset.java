package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.y5neko.amiya.handler.StringArrayTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName("assets")
public class Asset {
    @TableId
    private Long id;
    private String name;
    private String ip;
    private String domain;
    private Integer port;
    private String protocol;

    @TableField(typeHandler = StringArrayTypeHandler.class)
    private String[] tags;  // tags需要存储为json字符串

    private Long ownerId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

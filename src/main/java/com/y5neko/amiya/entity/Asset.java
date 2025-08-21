package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.y5neko.amiya.handler.StringArrayTypeHandler;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@TableName(value = "assets", autoResultMap = true)
public class Asset {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String ip;
    private String domain;
    private Integer port;
    private String protocol;

    @TableField(typeHandler = StringArrayTypeHandler.class)
    private String[] tags;

    private Long ownerId;
    private String status;

    @TableField(fill = FieldFill.INSERT)  // 插入时自动填充
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updatedAt;
}

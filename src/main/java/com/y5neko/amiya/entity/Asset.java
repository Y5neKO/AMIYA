package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("roles")
public class Role {
    @TableId
    private Long id;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

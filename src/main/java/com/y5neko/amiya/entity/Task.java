package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@TableName("tasks")
public class Task {
    @TableId
    private Long id;
    private String name;
    private Long assetId;
    private String scanType;
    private String scheduleType;
    private String cronExpr;
    private String status;
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)  // 插入时自动填充
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充
    private LocalDateTime updatedAt;
}

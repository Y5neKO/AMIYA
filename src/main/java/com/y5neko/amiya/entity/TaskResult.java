package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.y5neko.amiya.handler.MapJsonbTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务结果实体类
 */
@Data
@TableName("task_results")
public class TaskResult {
    @TableId
    private Long id;
    private Long taskId;
    private Long assetId;
    private String status;

    @TableField(typeHandler = MapJsonbTypeHandler.class)
    private Map<String,Object> rawResult;   // 需要存储为json字符串

    private String summary;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
}

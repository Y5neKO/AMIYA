package com.y5neko.amiya.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报告实体类
 */
@Data
@TableName("reports")
public class Report {
    @TableId
    private Long id;
    private Long taskId;
    private String filePath;
    private String format;
    private LocalDateTime generatedAt;
}

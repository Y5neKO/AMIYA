package com.y5neko.amiya.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.y5neko.amiya.entity.Report;

public interface ReportService {

    Report getById(Long id);

    void delete(Long id);

    /**
     * 分页查询报告，普通用户只能看自己创建的任务报告
     * @param page 当前页
     * @param size 每页条数
     * @param userId 普通用户ID，null 表示管理员可查看所有
     */
    Page<Report> getPage(long page, long size, Long userId);
}

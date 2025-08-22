package com.y5neko.amiya.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 统一分页响应类
 * @param <T> 数据类型
 */
@Setter
@Getter
public class PageResponse<T> {

    private long current;   // 当前页
    private long size;      // 每页条数
    private long total;     // 总条数
    private long pages;     // 总页数
    private List<T> records; // 当前页数据

    public PageResponse(long current, long size, long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = (total + size - 1) / size;
        this.records = records;
    }

}

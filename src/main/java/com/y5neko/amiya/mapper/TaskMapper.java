package com.y5neko.amiya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.y5neko.amiya.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}

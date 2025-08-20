package com.y5neko.amiya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.y5neko.amiya.entity.TaskResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务结果映射器接口
 */
@Mapper
public interface TaskResultMapper extends BaseMapper<TaskResult> {
}

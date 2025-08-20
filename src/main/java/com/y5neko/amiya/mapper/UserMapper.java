package com.y5neko.amiya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.y5neko.amiya.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户映射器接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

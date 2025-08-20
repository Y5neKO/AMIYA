package com.y5neko.amiya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.y5neko.amiya.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色映射器接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}

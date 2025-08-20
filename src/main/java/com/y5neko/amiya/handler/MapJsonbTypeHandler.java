package com.y5neko.amiya.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Map;

/**
 * 自定义类型处理器，用于将 Map 转换为 JSON 字符串存储在数据库中
 */
public class MapJsonbTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    private static final TypeReference<Map<String, Object>> TYPE_REF = new TypeReference<>() {};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, JSON.toJSONString(parameter), Types.OTHER);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return json == null ? null : JSON.parseObject(json, TYPE_REF);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json, TYPE_REF);
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json, TYPE_REF);
    }
}

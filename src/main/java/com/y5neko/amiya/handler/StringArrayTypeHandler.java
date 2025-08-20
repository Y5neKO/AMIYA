package com.y5neko.amiya.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * 自定义类型处理器，用于将字符串数组转换为数据库中的数组类型
 */
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setArray(i, ps.getConnection().createArrayOf("text", parameter));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        return array == null ? null : (String[]) array.getArray();
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        return array == null ? null : (String[]) array.getArray();
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        return array == null ? null : (String[]) array.getArray();
    }
}

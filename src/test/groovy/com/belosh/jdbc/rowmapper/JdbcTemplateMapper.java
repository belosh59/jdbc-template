package com.belosh.jdbc.rowmapper;

import com.belosh.jdbc.entity.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTemplateMapper implements IRowMapper<JdbcTemplate>{
    @Override
    public JdbcTemplate mapRow(ResultSet resultSet) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setFieldBoolean(resultSet.getBoolean("fboolean"));
        jdbcTemplate.setFieldDate(resultSet.getDate("fdate"));
        jdbcTemplate.setFieldDouble(resultSet.getDouble("fdouble"));
        jdbcTemplate.setFieldFloat(resultSet.getFloat("ffloat"));
        jdbcTemplate.setFieldInt(resultSet.getInt("fint"));
        jdbcTemplate.setFieldText(resultSet.getString("ftext"));
        return jdbcTemplate;
    }
}

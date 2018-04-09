package com.belosh.jdbc_template.rowmapper;

import com.belosh.jdbc_template.entity.TestVO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestVOMapper implements RowMapper<TestVO> {
    @Override
    public TestVO mapRow(ResultSet resultSet) throws SQLException {
        TestVO testVO = new TestVO();
        testVO.setFieldBoolean(resultSet.getBoolean("fboolean"));
        testVO.setFieldDouble(resultSet.getDouble("fdouble"));
        testVO.setFieldFloat(resultSet.getFloat("ffloat"));
        testVO.setFieldInt(resultSet.getInt("fint"));
        testVO.setFieldText(resultSet.getString("ftext"));
        testVO.setFieldDate(resultSet.getDate("fdate"));
        return testVO;
    }
}

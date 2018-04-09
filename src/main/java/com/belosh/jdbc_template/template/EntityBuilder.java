package com.belosh.jdbc_template.template;

import com.belosh.jdbc_template.rowmapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntityBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    <T> List<T> getEntities(ResultSet resultSet, RowMapper<T> rowMapper) {
        try {
            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (SQLException e) {
            logger.error("Failed to get row from result set");
            throw new RuntimeException(e);
        }
    }

    <T> T getEntity(ResultSet resultSet, RowMapper<T> rowMapper) {
        try {
            if (resultSet.next()) {
                return rowMapper.mapRow(resultSet);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Failed to get row from result set");
            throw new RuntimeException(e);
        }
    }
}

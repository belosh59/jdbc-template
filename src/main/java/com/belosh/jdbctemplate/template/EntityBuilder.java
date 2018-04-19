package com.belosh.jdbctemplate.template;

import com.belosh.jdbctemplate.exception.EmptyResultDataAccessException;
import com.belosh.jdbctemplate.exception.EntityBuilderException;
import com.belosh.jdbctemplate.exception.IncorrectResultSizeDataAccessException;
import com.belosh.jdbctemplate.rowmapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntityBuilder {

    private final static Logger logger = LoggerFactory.getLogger(EntityBuilder.class);

    static <T> List<T> mapEntities(ResultSet resultSet, RowMapper<T> rowMapper) {
        try {
            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }

            return result;
        } catch (SQLException e) {
            logger.error("Failed to process result set");
            throw new EntityBuilderException("Failed to process result set", e);
        }
    }

    static <T> T mapEntity(ResultSet resultSet, RowMapper<T> rowMapper) {
        try {
            if (resultSet.next()) {
                T entity = rowMapper.mapRow(resultSet);
                if (resultSet.next()) {
                    logger.error("More then one row in result set");
                    throw new IncorrectResultSizeDataAccessException("More then one row in result set");
                }

                return entity;
            } else {
                logger.error("Empty result set");
                throw new EmptyResultDataAccessException("Empty result set");
            }
        } catch (SQLException e) {
            logger.error("Failed to process result set");
            throw new EntityBuilderException("Failed to process result set", e);
        }
    }
}

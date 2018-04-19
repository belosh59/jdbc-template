package com.belosh.jdbctemplate.template;

import com.belosh.jdbctemplate.exception.QueryExecutorException;
import com.belosh.jdbctemplate.rowmapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class QueryExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    int executeUpdate(DataSource dataSource, String query, List<?> param) {
        String placeholderQuery = Parser.getPlaceholderQuery(query);
        long startExecution = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = getPreparedStatement(connection, placeholderQuery, param)) {

            logger.info("SQL: {}", statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", placeholderQuery);
            throw new QueryExecutorException("Failed to execute update", e);
        } finally {
            logger.debug("Execution time: {} milliseconds", System.currentTimeMillis() - startExecution);
        }
    }

    <T> List<T> executeQueryForList(DataSource dataSource, RowMapper<T> rowMapper, String query, List<?> param) {
        long startExecution = System.currentTimeMillis();
        String placeholderQuery = Parser.getPlaceholderQuery(query);

        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = getPreparedStatement(connection, placeholderQuery, param);
            ResultSet resultSet = statement.executeQuery()) {

            logger.info("SQL: {}", statement);
            return EntityBuilder.mapEntities(resultSet, rowMapper);
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", placeholderQuery);
            throw new QueryExecutorException("Failed to execute query", e);
        } finally {
            logger.debug("Execution time: {} milliseconds", System.currentTimeMillis() - startExecution);
        }
    }

    <T> T executeQueryForObject(DataSource dataSource, RowMapper<T> rowMapper, String query, List<?> param) {
        long startExecution = System.currentTimeMillis();
        String placeholderQuery = Parser.getPlaceholderQuery(query);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = getPreparedStatement(connection, placeholderQuery, param);
             ResultSet resultSet = statement.executeQuery()) {

            logger.info("SQL: {}", statement);
            return EntityBuilder.mapEntity(resultSet, rowMapper);
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", placeholderQuery);
            throw new QueryExecutorException("Failed to execute query", e);
        } finally {
            logger.debug("Execution time: {} milliseconds", System.currentTimeMillis() - startExecution);
        }
    }

    private PreparedStatement getPreparedStatement(Connection connection, String query, List<?> param) {
        try {
            PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            int index = 1;

            for (Object value : param) {
                if (value == null){
                    // Below method will set default values to fields depends on datatype
                    statement.setObject(index, null);
                } else {
                    Class valueClass = value.getClass();

                    if (Boolean.class.equals(valueClass)) {
                        statement.setBoolean(index, (boolean) value);
                    } else if (Integer.class.equals(valueClass)) {
                        statement.setInt(index, (int) value);
                    } else if (Double.class.equals(valueClass)) {
                        statement.setDouble(index, (double) value);
                    } else if (Long.class.equals(valueClass)) {
                        statement.setLong(index, (long) value);
                    } else if (Short.class.equals(valueClass)) {
                        statement.setShort(index, (short) value);
                    } else if (Byte.class.equals(valueClass)) {
                        statement.setBytes(index, (byte[]) value);
                    } else if (Float.class.equals(valueClass)) {
                        statement.setFloat(index, (float) value);
                    } else if (LocalDateTime.class == valueClass) {
                        statement.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
                    } else if (String.class == valueClass) {
                        statement.setString(index, (String) value);
                    } else if (JDBCType.class == valueClass) {
                        // Set null value
                        statement.setNull(index, ((JDBCType)value).getVendorTypeNumber());
                    }
                }
                index++;
            }

            return statement;
        } catch (SQLException e) {
            logger.error("Failed to set parameters into query: {}", query);
            logger.error("Parameters: {}", param);
            throw new QueryExecutorException("Failed to set parameters into query", e);
        }
    }
}

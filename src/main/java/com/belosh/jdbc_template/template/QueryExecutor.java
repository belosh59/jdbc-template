package com.belosh.jdbc_template.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class QueryExecutor {
    private DataSource dataSource;
    private Parser parser = new Parser();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    QueryExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    ResultSet getResultSet(String query, Map<String, ?> param) {
        String placeholderQuery = parser.getPlaceholderQuery(query, param);
        List<?> paramList = parser.getOrderedParamList(query, param);
        return executeQuery(placeholderQuery, paramList);
    }

    ResultSet getResultSet(String query, Object... args) {
        List<?> paramList = parser.getOrderedParamList(args);
        return executeQuery(query, paramList);
    }

    int executeUpdate(String query, Map<String, ?> param) {
        List<?> paramList = parser.getOrderedParamList(query, param);
        String placeholderQuery = parser.getPlaceholderQuery(query, param);
        return executeUpdate(placeholderQuery, paramList);
    }

    int executeUpdate(String query, Object... args) {
        List<?> paramList = parser.getOrderedParamList(args);
        return executeUpdate(query, paramList);
    }

    private int executeUpdate(String query, List<?> param) {
        long startExecution = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            setStatementVariables(statement, param);

            logger.info("SQL: {}", statement);
            int affectedRows = statement.executeUpdate();
            logger.debug("Statement executed in {} milliseconds", System.currentTimeMillis() - startExecution);

            return affectedRows;
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", query);
            throw new RuntimeException(e);
        }
    }

    private ResultSet executeQuery(String query, List<?> param) {
        long startExecution = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            setStatementVariables(statement, param);

            logger.info("SQL: {}", statement);
            ResultSet resultSet = statement.executeQuery();
            logger.debug("Statement executed in {} milliseconds", System.currentTimeMillis() - startExecution);

            return resultSet;
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", query);
            throw new RuntimeException(e);
        }
    }

    private void setStatementVariables(PreparedStatement statement, List<?> param) {
        try {
            int index = 1;
            for (Object value : param) {
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
                } else if (Date.class.equals(valueClass)) {
                    statement.setDate(index, (Date) value);
                } else {
                    statement.setString(index, (String) value);
                }
                index++;
            }
        } catch (SQLException e) {
            logger.error("Failed to set statement parameters: {}", statement);
            throw new RuntimeException(e);
        }
    }
}

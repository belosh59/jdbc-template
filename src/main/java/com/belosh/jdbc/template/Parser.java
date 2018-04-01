package com.belosh.jdbc.template;

import com.belosh.jdbc.rowmapper.IRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.*;

public class Parser {

    private DataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(Parser.class);

    protected Map<Integer, ?> getIndexValueMap(String query, Map<String, ?> param) {
        String paramName;
        int startKeyPosition = 0;
        int parameterIndex = 1;
        Map<Integer, Object> indexValueMap = new HashMap<>();

        while ((startKeyPosition = query.indexOf(':', startKeyPosition)) > 0) {
            startKeyPosition++;
            int endKeyPosition;
            if ((endKeyPosition = query.indexOf(',', startKeyPosition)) > 0) {
                paramName = query.substring(startKeyPosition, endKeyPosition);
            } else if ((endKeyPosition = query.indexOf(' ', startKeyPosition)) > 0) {
                paramName = query.substring(startKeyPosition, endKeyPosition);
            } else if  ((endKeyPosition = query.indexOf(')', startKeyPosition)) > 0) {
                paramName = query.substring(startKeyPosition, endKeyPosition);
            } else {
                paramName = query.substring(startKeyPosition);
            }
            indexValueMap.put(parameterIndex, param.get(paramName));
            parameterIndex++;
        }
        return indexValueMap;
    }

    protected Map<Integer, ?> getIndexValueMap(Object... args) {
        int parameterIndex = 1;
        Map<Integer, Object> indexValueMap = new HashMap<>();

        for(Object object : args) {
            indexValueMap.put(parameterIndex++, object);
        }
        return indexValueMap;
    }

    protected  ResultSet getResultSet(String query, Map<Integer, ?> paramIndexValue) {
        long startExecution = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            setStatementVariables(statement, paramIndexValue);
            logger.info("SQL: {}", statement);
            ResultSet resultSet = statement.executeQuery();
            logger.debug("Statement executed in {} milliseconds", System.currentTimeMillis() - startExecution);
            return resultSet;
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", query);
            throw new RuntimeException(e);
        }
    }

    protected int executeUpdate(String query, Map<Integer, ?> paramIndexValue) {
        long startExecution = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            setStatementVariables(statement, paramIndexValue);
            logger.info("SQL: {}", statement);
            int affectedRows = statement.executeUpdate();
            logger.debug("Statement executed in {} milliseconds", System.currentTimeMillis() - startExecution);
            return affectedRows;
        } catch (SQLException e) {
            logger.error("SQL Failed: {}", query);
            throw new RuntimeException(e);
        }
    }

    protected <T> List<T> getListOfEntities(ResultSet resultSet, IRowMapper<T> rowMapper) {
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

    protected <T> T getEntitry(ResultSet resultSet, IRowMapper<T> rowMapper) {
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

    protected  String replaceBindVarWirhPlaceholder(String query, Map<String, ?> param) {
        for (String paramName : param.keySet()) {
            query = query.replaceFirst(":" + paramName, "?");
        }
        return query;
    }

    protected void setStatementVariables(PreparedStatement statement, Map<Integer, ?> paramIndexValue) {
        try {
            for (int index : paramIndexValue.keySet()) {
                Object paramValue = paramIndexValue.get(index);
                Class paramValueClass = paramValue.getClass();
                if (Boolean.class.equals(paramValueClass)) {
                    statement.setBoolean(index, (boolean) paramValue);
                } else if (Integer.class.equals(paramValueClass)) {
                    statement.setInt(index, (int) paramValue);
                } else if (Double.class.equals(paramValueClass)) {
                    statement.setDouble(index, (double) paramValue);
                } else if (Long.class.equals(paramValueClass)) {
                    statement.setLong(index, (long) paramValue);
                } else if (Short.class.equals(paramValueClass)) {
                    statement.setShort(index, (short) paramValue);
                } else if (Byte.class.equals(paramValueClass)) {
                    statement.setBytes(index, (byte[]) paramValue);
                } else if (Float.class.equals(paramValueClass)) {
                    statement.setFloat(index, (float) paramValue);
                } else if (Date.class.equals(paramValueClass)) {
                    statement.setDate(index, (Date) paramValue);
                } else {
                    statement.setString(index, (String) paramValue);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to set statement variables: {}", statement);
            throw new RuntimeException(e);
        }
    }

    protected void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

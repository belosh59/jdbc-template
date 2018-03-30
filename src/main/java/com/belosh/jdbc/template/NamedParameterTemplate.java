package com.belosh.jdbc.template;

import com.belosh.jdbc.rowmapper.IRowMapper;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class NamedParameterTemplate {
    private Parser parser = new Parser();

    public void setDataSource(DataSource dataSource) {
        parser.setDataSource(dataSource);
    }

    public <T> List<T> query(String query, IRowMapper<T> rowMapper, Map<String, ?> param){
        Map<Integer, ?> indexValueMap = parser.getIndexValueMap(query, param);
        String placeholderQuery = parser.replaceBindVarWirhPlaceholder(query, param);
        ResultSet resultSet = parser.getResultSet(placeholderQuery, indexValueMap);
        return parser.getListOfEntities(resultSet, rowMapper);
    }

    public <T> List<T> query(String query, IRowMapper<T> rowMapper, Object... args){
        Map<Integer, ?> indexValueMap = parser.getIndexValueMap(args);
        ResultSet resultSet = parser.getResultSet(query, indexValueMap);
        return parser.getListOfEntities(resultSet, rowMapper);
    }

    public <T> T queryForObject(String query, IRowMapper<T> rowMapper, Map<String, ?> param) {
        Map<Integer, ?> indexValueMap = parser.getIndexValueMap(query, param);
        String placeholderQuery = parser.replaceBindVarWirhPlaceholder(query, param);
        ResultSet resultSet = parser.getResultSet(placeholderQuery, indexValueMap);
        return parser.getEntitry(resultSet, rowMapper);
    }

    public <T> T queryForObject(String query, IRowMapper<T> rowMapper, Object... args) {
        Map<Integer, ?> indexValueMap = parser.getIndexValueMap(args);
        ResultSet resultSet = parser.getResultSet(query, indexValueMap);
        return parser.getEntitry(resultSet, rowMapper);
    }

    public <T> int update(String query, Map<String, ?> param) {
        Map<Integer, ?> indexValueMap = parser.getIndexValueMap(query, param);
        String placeholderQuery = parser.replaceBindVarWirhPlaceholder(query, param);
        return parser.executeUpdate(placeholderQuery, indexValueMap);
    }

    public <T> int update(String query, Object... args){
        Map<Integer, ?> indexValueMap = parser.getIndexValueMap(args);
        return parser.executeUpdate(query, indexValueMap);
    }


}

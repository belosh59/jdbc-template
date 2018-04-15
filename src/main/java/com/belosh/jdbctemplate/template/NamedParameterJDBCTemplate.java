package com.belosh.jdbctemplate.template;

import com.belosh.jdbctemplate.rowmapper.RowMapper;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class NamedParameterJDBCTemplate {
    private DataSource dataSource;
    private QueryExecutor queryExecutor = new QueryExecutor();

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(String query, RowMapper<T> rowMapper, Map<String, ?> param){
        List<?> parameters = Parser.getOrderedParamList(query, param);
        return queryExecutor.executeQueryForList(dataSource, rowMapper, query, parameters);
    }

    public <T> T queryForObject(String query, RowMapper<T> rowMapper, Map<String, ?> param) {
        List<?> parameters = Parser.getOrderedParamList(query, param);
        return queryExecutor.executeQueryForObject(dataSource, rowMapper, query, parameters);
    }

    public int update(String query, Map<String, ?> param) {
        List<?> parameters = Parser.getOrderedParamList(query, param);
        return queryExecutor.executeUpdate(dataSource, query, parameters);
    }

    public <T> List<T> query(String query, RowMapper<T> rowMapper, Object... args){
        List<?> parameters = Parser.getOrderedParamList(args);
        return queryExecutor.executeQueryForList(dataSource, rowMapper, query, parameters);
    }

    public <T> T queryForObject(String query, RowMapper<T> rowMapper, Object... args) {
        List<?> parameters = Parser.getOrderedParamList(args);
        return queryExecutor.executeQueryForObject(dataSource, rowMapper, query, parameters);
    }

    public int update(String query, Object... args){
        List<?> parameters = Parser.getOrderedParamList(args);
        return queryExecutor.executeUpdate(dataSource, query, parameters);
    }
}

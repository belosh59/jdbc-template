package com.belosh.jdbc_template.template;

import com.belosh.jdbc_template.rowmapper.RowMapper;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class NamedParameterTemplate {
    private DataSource dataSource;
    private QueryExecutor queryExecutor;
    private EntityBuilder entityBuilder;

    public NamedParameterTemplate(DataSource dataSource) {
        this.queryExecutor = new QueryExecutor(dataSource);
        this.entityBuilder = new EntityBuilder();
    }

    public <T> List<T> query(String query, RowMapper<T> rowMapper, Map<String, ?> param){
        return entityBuilder.getEntities(queryExecutor.getResultSet(query, param), rowMapper);
    }

    public <T> List<T> query(String query, RowMapper<T> rowMapper, Object... args){
        return entityBuilder.getEntities(queryExecutor.getResultSet(query, args), rowMapper);
    }

    public <T> T queryForObject(String query, RowMapper<T> rowMapper, Map<String, ?> param) {
        return entityBuilder.getEntity(queryExecutor.getResultSet(query, param), rowMapper);
    }

    public <T> T queryForObject(String query, RowMapper<T> rowMapper, Object... args) {
        return entityBuilder.getEntity(queryExecutor.getResultSet(query, args), rowMapper);
    }

    public int update(String query, Map<String, ?> param) {
        return queryExecutor.executeUpdate(query, param);
    }

    public int update(String query, Object... args){
        return queryExecutor.executeUpdate(query, args);
    }
}

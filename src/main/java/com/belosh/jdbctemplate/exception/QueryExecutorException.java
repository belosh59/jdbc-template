package com.belosh.jdbctemplate.exception;

public class QueryExecutorException extends RuntimeException {
    public QueryExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}

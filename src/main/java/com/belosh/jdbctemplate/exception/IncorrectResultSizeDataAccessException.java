package com.belosh.jdbctemplate.exception;

public class IncorrectResultSizeDataAccessException extends RuntimeException {
    public IncorrectResultSizeDataAccessException(String message) {
        super(message);
    }
}

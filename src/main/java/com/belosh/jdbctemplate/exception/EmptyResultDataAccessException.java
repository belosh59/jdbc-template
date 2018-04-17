package com.belosh.jdbctemplate.exception;

public class EmptyResultDataAccessException extends RuntimeException {
    public EmptyResultDataAccessException(String message) {
        super(message);
    }
}

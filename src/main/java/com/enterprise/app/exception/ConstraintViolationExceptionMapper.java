package com.enterprise.app.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private final GlobalExceptionMapper globalExceptionMapper;

    public ConstraintViolationExceptionMapper(GlobalExceptionMapper globalExceptionMapper) {
        this.globalExceptionMapper = globalExceptionMapper;
    }

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return globalExceptionMapper.toResponse(exception);
    }
}

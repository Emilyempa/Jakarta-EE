package org.example.api;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<FieldError> fieldErrors = exception.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString();
                    if (field.contains(".")) {
                        field = field.substring(field.lastIndexOf('.') + 1);
                    }
                    return new FieldError(field, violation.getMessage());
                })
                .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.of(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Validation failed",
                fieldErrors
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
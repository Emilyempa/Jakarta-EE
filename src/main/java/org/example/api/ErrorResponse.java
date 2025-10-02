package org.example.api;

import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        long timestamp,
        List<FieldError> filedErrors
) {
    public static ErrorResponse of(int status, String error, List<FieldError> fieldErrors) {
        return new ErrorResponse(status, error, System.currentTimeMillis(), fieldErrors);
    }

    public static ErrorResponse of(int status, String error) {
        return new ErrorResponse(status, error, System.currentTimeMillis(), null);
    }
}

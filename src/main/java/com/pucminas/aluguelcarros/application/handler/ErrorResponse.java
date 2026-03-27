package com.pucminas.aluguelcarros.application.handler;

import io.micronaut.serde.annotation.Serdeable;
import java.time.LocalDateTime;

@Serdeable
public record ErrorResponse(
        int status,
        String error,
        String message,
        String timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now().toString());
    }
}

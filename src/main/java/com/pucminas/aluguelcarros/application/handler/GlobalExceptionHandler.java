package com.pucminas.aluguelcarros.application.handler;

import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import jakarta.validation.ConstraintViolationException;

@Controller
public class GlobalExceptionHandler {

    @Error(global = true, exception = ResourceNotFoundException.class)
    public HttpResponse<ErrorResponse> handleNotFound(HttpRequest<?> request, ResourceNotFoundException ex) {
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(404, "Not Found", ex.getMessage())
        );
    }

    @Error(global = true, exception = BusinessException.class)
    public HttpResponse<ErrorResponse> handleBusiness(HttpRequest<?> request, BusinessException ex) {
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(400, "Bad Request", ex.getMessage())
        );
    }

    @Error(global = true, exception = ConstraintViolationException.class)
    public HttpResponse<ErrorResponse> handleValidation(HttpRequest<?> request, ConstraintViolationException ex) {
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(400, "Validation Error", ex.getMessage())
        );
    }
}

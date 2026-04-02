package com.goylik.order_service.controller.advice;

import com.goylik.order_service.exception.client.UserServiceUnavailableException;
import com.goylik.order_service.model.dto.response.ErrorResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ClientControllerAdvice {
    @ExceptionHandler(UserServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleUserServiceUnavailableException(UserServiceUnavailableException ex) {
        log.warn("User service is unavailable: {}", ex.getMessage());
        return ErrorResponse.of(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "User Service is unavailable",
                ex.getMessage()
        );
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleFeignException(FeignException ex) {
        log.warn("User service error: {}", ex.getMessage());
        return ErrorResponse.of(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "User service error",
                ex.getMessage()
        );
    }
}
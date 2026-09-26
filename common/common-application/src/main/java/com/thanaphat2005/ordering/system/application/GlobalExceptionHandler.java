package com.thanaphat2005.ordering.system.application;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleException(Exception exception){
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error occurred.")
                .build();
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleExceptionBadRequest(ValidationException validationException){
        log.error(validationException.getMessage(), validationException);
        ErrorDto errorDto;
        if(validationException instanceof ConstraintViolationException){
            String violationMessage = extractViolationsFromException((ConstraintViolationException) validationException);
            log.error("Constraint violation: {}", violationMessage);
            errorDto = ErrorDto.builder().code(HttpStatus.BAD_REQUEST.getReasonPhrase()).message(violationMessage).build();
        }else{
            String exceptionMessage = validationException.getMessage();
            log.error(exceptionMessage, validationException);
            errorDto = ErrorDto.builder().code(HttpStatus.BAD_REQUEST.getReasonPhrase()).message(exceptionMessage).build();
        }
        return errorDto;
    }

    private String extractViolationsFromException(ConstraintViolationException validationException) {
        return  validationException.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("--"));
    }


}

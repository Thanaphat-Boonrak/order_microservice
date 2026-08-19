package com.thanaphat2005.food.ordering.system.order.service.application.exception.handler;


import com.thanaphat2005.food.ordering.system.order.service.ai.exception.AIOrderNoteInterpreterException;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.thanaphat2005.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.thanaphat2005.ordering.system.application.ErrorDto;
import com.thanaphat2005.ordering.system.application.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;

@RestControllerAdvice
@Slf4j
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(value = {OrderDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleExceptionBadRequest(OrderDomainException orderDomainException){
        log.error(orderDomainException.getMessage(), orderDomainException);
        return ErrorDto.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(orderDomainException.getMessage())
                .build();
    }


    @ExceptionHandler(value = {OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleExceptionNotFound(OrderNotFoundException orderNotFoundException){
        log.error(orderNotFoundException.getMessage(), orderNotFoundException);
        return ErrorDto.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(orderNotFoundException.getMessage())
                .build();
    }


    @ExceptionHandler(value = {AIOrderNoteInterpreterException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleExceptionAIServerError(AIOrderNoteInterpreterException aiOrderNoteInterpreterException){
        log.error(aiOrderNoteInterpreterException.getMessage(), aiOrderNoteInterpreterException);
        return ErrorDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(aiOrderNoteInterpreterException.getMessage())
                .build();
    }

    @ExceptionHandler(value = {TransactionException.class , UndeclaredThrowableException.class})
    public ErrorDto handleExceptionTransaction(Exception exception){
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);

        if (rootCause instanceof OrderNotFoundException orderNotFoundException) {
            return handleExceptionNotFound(orderNotFoundException);
        }

        if (rootCause instanceof OrderDomainException orderDomainException) {
            return handleExceptionBadRequest(orderDomainException);
        }

        log.error("Transaction exception occurred with cause: {}", rootCause.getMessage(), exception);
        return ErrorDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected transaction error occurred.")
                .build();
    }
}

package com.thanaphat2005.food.ordering.system.customer.service.application.handler;

import com.thanaphat2005.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.thanaphat2005.ordering.system.application.ErrorDto;
import com.thanaphat2005.ordering.system.application.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CustomerGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {CustomerDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(CustomerDomainException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder().code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage()).build();
    }

}

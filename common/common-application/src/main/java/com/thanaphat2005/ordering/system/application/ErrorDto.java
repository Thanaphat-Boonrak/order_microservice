package com.thanaphat2005.ordering.system.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ErrorDto {
    private String message;
    private String code;
}

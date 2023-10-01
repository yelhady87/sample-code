package com.incorta.webapi.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {
    private final String errorMessage;
}

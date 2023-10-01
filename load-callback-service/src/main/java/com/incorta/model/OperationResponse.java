package com.incorta.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationResponse {
    private final String message;
}

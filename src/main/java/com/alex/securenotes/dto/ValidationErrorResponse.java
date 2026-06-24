package com.alex.securenotes.dto;

import java.util.Map;

public record ValidationErrorResponse(
        Map<String, String> errors
) {
}
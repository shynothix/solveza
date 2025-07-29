package com.shinkaji.solveza.api.shared.presentation.exception;

import java.util.Map;

public record ValidationErrorResponse(
    String code, String message, Map<String, String> fieldErrors) {}

package com.shinkaji.solveza.api.usermanagement.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
    @NotBlank(message = "権限名は必須です") String name,
    @NotBlank(message = "リソースは必須です") String resource,
    @NotBlank(message = "アクションは必須です") String action) {}

package com.shinkaji.solveza.api.usermanagement.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(@NotBlank(message = "ロール名は必須です") String name, String description) {}

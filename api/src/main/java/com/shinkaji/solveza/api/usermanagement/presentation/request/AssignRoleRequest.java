package com.shinkaji.solveza.api.usermanagement.presentation.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignRoleRequest(@NotNull(message = "ロールIDは必須です") UUID roleId) {}

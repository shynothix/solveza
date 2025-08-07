package com.shinkaji.solveza.api.usermanagement.presentation.request;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record DefinePermissionsRequest(
    @NotNull(message = "権限IDセットは必須です") Set<UUID> permissionIds) {}

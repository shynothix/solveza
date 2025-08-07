package com.shinkaji.solveza.api.usermanagement.presentation.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record RoleResponse(
    UUID id, String name, String description, Set<UUID> permissionIds, LocalDateTime createdAt) {}

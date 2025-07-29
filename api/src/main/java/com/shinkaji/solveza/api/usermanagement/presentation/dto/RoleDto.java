package com.shinkaji.solveza.api.usermanagement.presentation.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record RoleDto(
    UUID id, String name, String description, Set<UUID> permissionIds, LocalDateTime createdAt) {}

package com.shinkaji.solveza.api.usermanagement.presentation.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDto(
    UUID id,
    String provider,
    String externalId,
    String name,
    String email,
    Set<UUID> roleIds,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

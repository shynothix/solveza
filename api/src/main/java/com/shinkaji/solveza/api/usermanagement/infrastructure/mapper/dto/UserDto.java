package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto;

import java.time.LocalDateTime;

public record UserDto(
    String id,
    String provider,
    String externalId,
    String name,
    String email,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

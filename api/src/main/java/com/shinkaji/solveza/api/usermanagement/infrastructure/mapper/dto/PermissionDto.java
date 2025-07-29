package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto;

import java.time.LocalDateTime;

public record PermissionDto(
    String id, String name, String resource, String action, LocalDateTime createdAt) {}

package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto;

import java.time.LocalDateTime;

public record RoleDto(String id, String name, String description, LocalDateTime createdAt) {}

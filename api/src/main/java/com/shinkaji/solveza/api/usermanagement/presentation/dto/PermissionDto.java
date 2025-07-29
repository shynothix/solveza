package com.shinkaji.solveza.api.usermanagement.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionDto(
    UUID id, String name, String resource, String action, LocalDateTime createdAt) {}

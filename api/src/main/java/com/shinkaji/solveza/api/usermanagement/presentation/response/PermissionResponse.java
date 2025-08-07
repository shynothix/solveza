package com.shinkaji.solveza.api.usermanagement.presentation.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionResponse(
    UUID id, String name, String resource, String action, LocalDateTime createdAt) {}

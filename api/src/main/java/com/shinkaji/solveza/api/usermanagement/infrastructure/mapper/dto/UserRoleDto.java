package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto;

import java.time.LocalDateTime;

public record UserRoleDto(String userId, String roleId, LocalDateTime assignedAt) {}

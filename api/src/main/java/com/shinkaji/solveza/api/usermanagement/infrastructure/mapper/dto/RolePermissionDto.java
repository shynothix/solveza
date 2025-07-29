package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto;

import java.time.LocalDateTime;

public record RolePermissionDto(String roleId, String permissionId, LocalDateTime grantedAt) {}

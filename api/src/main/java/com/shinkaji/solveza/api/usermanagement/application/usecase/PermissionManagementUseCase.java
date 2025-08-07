package com.shinkaji.solveza.api.usermanagement.application.usecase;

import com.shinkaji.solveza.api.usermanagement.presentation.dto.PermissionDto;
import java.util.List;
import java.util.UUID;

public interface PermissionManagementUseCase {

  PermissionDto createPermission(String name, String resource, String action);

  PermissionDto getPermissionById(UUID permissionId);

  PermissionDto getPermissionByName(String name);

  List<PermissionDto> getAllPermissions();

  void deletePermission(UUID permissionId);
}

package com.shinkaji.solveza.api.usermanagement.application.usecase;

import com.shinkaji.solveza.api.usermanagement.presentation.dto.RoleDto;
import java.util.List;
import java.util.UUID;

public interface RoleManagementUseCase {

  RoleDto createRole(String name, String description);

  RoleDto getRoleById(UUID roleId);

  RoleDto getRoleByName(String name);

  List<RoleDto> getAllRoles();

  void deleteRole(UUID roleId);
}

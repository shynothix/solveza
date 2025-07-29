package com.shinkaji.solveza.api.usermanagement.domain.service;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;

public interface UserValidationService {

  void validateUserExists(UserId userId);

  void validateRoleExists(RoleId roleId);

  void validatePermissionExists(PermissionId permissionId);

  void validateUserNotExists(UserId userId);

  void validateRoleNotExists(String roleName);

  void validatePermissionNotExists(String permissionName);
}

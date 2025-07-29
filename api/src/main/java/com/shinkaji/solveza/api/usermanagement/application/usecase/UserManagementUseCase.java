package com.shinkaji.solveza.api.usermanagement.application.usecase;

import com.shinkaji.solveza.api.usermanagement.application.command.AssignRoleCommand;
import com.shinkaji.solveza.api.usermanagement.application.command.DefinePermissionsCommand;
import com.shinkaji.solveza.api.usermanagement.application.command.RegisterUserCommand;
import com.shinkaji.solveza.api.usermanagement.application.query.GetUsersQuery;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.UserDto;
import java.util.List;
import java.util.UUID;

public interface UserManagementUseCase {

  UserDto registerOrUpdateUser(RegisterUserCommand command);

  void assignRole(AssignRoleCommand command);

  void defineRolePermissions(DefinePermissionsCommand command);

  UserDto getUserById(UUID userId);

  List<UserDto> getUsers(GetUsersQuery query);
}

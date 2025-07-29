package com.shinkaji.solveza.api.usermanagement.application.usecase;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.shared.domain.exception.UserNotFoundException;
import com.shinkaji.solveza.api.usermanagement.application.command.AssignRoleCommand;
import com.shinkaji.solveza.api.usermanagement.application.command.DefinePermissionsCommand;
import com.shinkaji.solveza.api.usermanagement.application.command.RegisterUserCommand;
import com.shinkaji.solveza.api.usermanagement.application.query.GetUsersQuery;
import com.shinkaji.solveza.api.usermanagement.domain.model.Role;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserSearchCriteria;
import com.shinkaji.solveza.api.usermanagement.domain.service.UserValidationService;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserManagementUseCaseImpl implements UserManagementUseCase {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserValidationService userValidationService;

  public UserManagementUseCaseImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      UserValidationService userValidationService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.userValidationService = userValidationService;
  }

  @Override
  public UserDto registerOrUpdateUser(RegisterUserCommand command) {
    Provider provider = new Provider(command.provider());

    Optional<User> existingUser =
        userRepository.findByProviderAndExternalId(provider, command.externalId());

    User user;
    if (existingUser.isPresent()) {
      // 既存ユーザーの更新
      user = existingUser.get();
      user.updateName(command.name());
      user.updateEmail(command.email());
    } else {
      // 新規ユーザーの作成
      user = User.create(provider, command.externalId(), command.name(), command.email());
    }

    userRepository.save(user);
    return toDto(user);
  }

  @Override
  public void assignRole(AssignRoleCommand command) {
    UserId userId = new UserId(command.userId());
    RoleId roleId = new RoleId(command.roleId());

    // ユーザーとロールの存在確認
    userValidationService.validateUserExists(userId);
    userValidationService.validateRoleExists(roleId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    user.assignRole(roleId);
    userRepository.save(user);
  }

  @Override
  public void defineRolePermissions(DefinePermissionsCommand command) {
    RoleId roleId = new RoleId(command.roleId());

    // ロールの存在確認
    userValidationService.validateRoleExists(roleId);

    // 権限の存在確認
    Set<PermissionId> permissionIds =
        command.permissionIds().stream().map(PermissionId::new).collect(Collectors.toSet());

    for (PermissionId permissionId : permissionIds) {
      userValidationService.validatePermissionExists(permissionId);
    }

    Role role =
        roleRepository
            .findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません: " + roleId));

    // 既存の権限をクリアして新しい権限を設定
    Set<PermissionId> currentPermissions = role.getPermissionIds();
    for (PermissionId currentPermission : currentPermissions) {
      role.revokePermission(currentPermission);
    }

    for (PermissionId permissionId : permissionIds) {
      role.grantPermission(permissionId);
    }

    roleRepository.save(role);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserById(UUID userId) {
    UserId userIdVO = new UserId(userId);
    User user =
        userRepository.findById(userIdVO).orElseThrow(() -> new UserNotFoundException(userIdVO));

    return toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getUsers(GetUsersQuery query) {
    UserSearchCriteria criteria = new UserSearchCriteria(query.provider(), query.externalId());
    List<User> users = userRepository.findByCriteria(criteria);

    return users.stream().map(this::toDto).collect(Collectors.toList());
  }

  private UserDto toDto(User user) {
    Set<UUID> roleIds = user.getRoleIds().stream().map(RoleId::value).collect(Collectors.toSet());

    return new UserDto(
        user.getId(),
        user.getProvider().name(),
        user.getExternalId(),
        user.getName(),
        user.getEmail(),
        roleIds,
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}

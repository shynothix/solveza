package com.shinkaji.solveza.api.usermanagement.domain.service;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.shared.domain.exception.UserNotFoundException;
import com.shinkaji.solveza.api.usermanagement.domain.repository.PermissionRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserValidationServiceImpl implements UserValidationService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  public UserValidationServiceImpl(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PermissionRepository permissionRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  @Override
  public void validateUserExists(UserId userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }
  }

  @Override
  public void validateRoleExists(RoleId roleId) {
    if (!roleRepository.existsById(roleId)) {
      throw new IllegalArgumentException("ロールが見つかりません: " + roleId);
    }
  }

  @Override
  public void validatePermissionExists(PermissionId permissionId) {
    if (!permissionRepository.existsById(permissionId)) {
      throw new IllegalArgumentException("権限が見つかりません: " + permissionId);
    }
  }

  @Override
  public void validateUserNotExists(UserId userId) {
    if (userRepository.existsById(userId)) {
      throw new IllegalArgumentException("ユーザーが既に存在します: " + userId);
    }
  }

  @Override
  public void validateRoleNotExists(String roleName) {
    if (roleRepository.existsByName(roleName)) {
      throw new IllegalArgumentException("ロールが既に存在します: " + roleName);
    }
  }

  @Override
  public void validatePermissionNotExists(String permissionName) {
    if (permissionRepository.existsByName(permissionName)) {
      throw new IllegalArgumentException("権限が既に存在します: " + permissionName);
    }
  }
}

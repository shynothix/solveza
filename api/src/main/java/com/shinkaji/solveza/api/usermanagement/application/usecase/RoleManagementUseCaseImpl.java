package com.shinkaji.solveza.api.usermanagement.application.usecase;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.usermanagement.domain.model.Role;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.domain.service.UserValidationService;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.RoleDto;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleManagementUseCaseImpl implements RoleManagementUseCase {

  private final RoleRepository roleRepository;
  private final UserValidationService userValidationService;

  public RoleManagementUseCaseImpl(
      RoleRepository roleRepository, UserValidationService userValidationService) {
    this.roleRepository = roleRepository;
    this.userValidationService = userValidationService;
  }

  @Override
  public RoleDto createRole(String name, String description) {
    // 重複チェック
    userValidationService.validateRoleNotExists(name);

    Role role = Role.create(name, description);
    roleRepository.save(role);

    return toDto(role);
  }

  @Override
  @Transactional(readOnly = true)
  public RoleDto getRoleById(UUID roleId) {
    RoleId roleIdVO = new RoleId(roleId);
    Role role =
        roleRepository
            .findById(roleIdVO)
            .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません: " + roleId));

    return toDto(role);
  }

  @Override
  @Transactional(readOnly = true)
  public RoleDto getRoleByName(String name) {
    Role role =
        roleRepository
            .findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません: " + name));

    return toDto(role);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoleDto> getAllRoles() {
    return roleRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  @Override
  public void deleteRole(UUID roleId) {
    RoleId roleIdVO = new RoleId(roleId);
    userValidationService.validateRoleExists(roleIdVO);
    roleRepository.delete(roleIdVO);
  }

  private RoleDto toDto(Role role) {
    Set<UUID> permissionIds =
        role.getPermissionIds().stream().map(PermissionId::value).collect(Collectors.toSet());

    return new RoleDto(
        role.getId(), role.getName(), role.getDescription(), permissionIds, role.getCreatedAt());
  }
}

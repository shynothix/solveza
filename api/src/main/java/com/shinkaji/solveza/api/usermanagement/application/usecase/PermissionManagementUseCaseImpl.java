package com.shinkaji.solveza.api.usermanagement.application.usecase;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.usermanagement.domain.model.Permission;
import com.shinkaji.solveza.api.usermanagement.domain.repository.PermissionRepository;
import com.shinkaji.solveza.api.usermanagement.domain.service.UserValidationService;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.PermissionDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionManagementUseCaseImpl implements PermissionManagementUseCase {

  private final PermissionRepository permissionRepository;
  private final UserValidationService userValidationService;

  public PermissionManagementUseCaseImpl(
      PermissionRepository permissionRepository, UserValidationService userValidationService) {
    this.permissionRepository = permissionRepository;
    this.userValidationService = userValidationService;
  }

  @Override
  public PermissionDto createPermission(String name, String resource, String action) {
    // 重複チェック
    userValidationService.validatePermissionNotExists(name);

    Permission permission = Permission.create(name, resource, action);
    permissionRepository.save(permission);

    return toDto(permission);
  }

  @Override
  @Transactional(readOnly = true)
  public PermissionDto getPermissionById(UUID permissionId) {
    PermissionId permissionIdVO = new PermissionId(permissionId);
    Permission permission =
        permissionRepository
            .findById(permissionIdVO)
            .orElseThrow(() -> new IllegalArgumentException("権限が見つかりません: " + permissionId));

    return toDto(permission);
  }

  @Override
  @Transactional(readOnly = true)
  public PermissionDto getPermissionByName(String name) {
    Permission permission =
        permissionRepository
            .findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("権限が見つかりません: " + name));

    return toDto(permission);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PermissionDto> getAllPermissions() {
    return permissionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  @Override
  public void deletePermission(UUID permissionId) {
    PermissionId permissionIdVO = new PermissionId(permissionId);
    userValidationService.validatePermissionExists(permissionIdVO);
    permissionRepository.delete(permissionIdVO);
  }

  private PermissionDto toDto(Permission permission) {
    return new PermissionDto(
        permission.getId(),
        permission.getName(),
        permission.getResource(),
        permission.getAction(),
        permission.getCreatedAt());
  }
}

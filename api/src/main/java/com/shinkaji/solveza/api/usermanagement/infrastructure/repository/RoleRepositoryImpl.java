package com.shinkaji.solveza.api.usermanagement.infrastructure.repository;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.usermanagement.domain.model.Role;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.RoleMapper;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.RoleDto;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.RolePermissionDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

  private final RoleMapper roleMapper;

  public RoleRepositoryImpl(RoleMapper roleMapper) {
    this.roleMapper = roleMapper;
  }

  @Override
  public Optional<Role> findById(RoleId roleId) {
    return roleMapper.findById(roleId.value().toString()).map(this::toDomain);
  }

  @Override
  public Optional<Role> findByName(String name) {
    return roleMapper.findByName(name).map(this::toDomain);
  }

  @Override
  public List<Role> findAll() {
    return roleMapper.findAll().stream().map(this::toDomain).collect(Collectors.toList());
  }

  @Override
  public void save(Role role) {
    RoleDto roleDto = toDto(role);

    if (roleMapper.existsById(role.getId().toString())) {
      roleMapper.update(roleDto);
    } else {
      roleMapper.insert(roleDto);
    }

    // Handle role permissions
    saveRolePermissions(role);
  }

  @Override
  public void delete(RoleId roleId) {
    roleMapper.delete(roleId.value().toString());
  }

  @Override
  public boolean existsById(RoleId roleId) {
    return roleMapper.existsById(roleId.value().toString());
  }

  @Override
  public boolean existsByName(String name) {
    return roleMapper.existsByName(name);
  }

  private Role toDomain(RoleDto dto) {
    Set<PermissionId> permissionIds =
        roleMapper.findRolePermissionsByRoleId(dto.id()).stream()
            .map(
                rolePermissionDto ->
                    new PermissionId(java.util.UUID.fromString(rolePermissionDto.permissionId())))
            .collect(Collectors.toSet());

    return Role.reconstruct(
        java.util.UUID.fromString(dto.id()),
        dto.createdAt(),
        dto.createdAt(), // Note: Roles don't have updatedAt in the table
        dto.name(),
        dto.description(),
        permissionIds);
  }

  private RoleDto toDto(Role role) {
    return new RoleDto(
        role.getId().toString(), role.getName(), role.getDescription(), role.getCreatedAt());
  }

  private void saveRolePermissions(Role role) {
    // First, get existing permissions
    Set<PermissionId> existingPermissionIds =
        roleMapper.findRolePermissionsByRoleId(role.getId().toString()).stream()
            .map(
                rolePermissionDto ->
                    new PermissionId(java.util.UUID.fromString(rolePermissionDto.permissionId())))
            .collect(Collectors.toSet());

    Set<PermissionId> currentPermissionIds = role.getPermissionIds();

    // Add new permissions
    for (PermissionId permissionId : currentPermissionIds) {
      if (!existingPermissionIds.contains(permissionId)) {
        RolePermissionDto rolePermissionDto =
            new RolePermissionDto(
                role.getRoleId().value().toString(),
                permissionId.value().toString(),
                LocalDateTime.now());
        roleMapper.insertRolePermission(rolePermissionDto);
      }
    }

    // Remove revoked permissions
    for (PermissionId permissionId : existingPermissionIds) {
      if (!currentPermissionIds.contains(permissionId)) {
        roleMapper.deleteRolePermission(
            role.getRoleId().value().toString(), permissionId.value().toString());
      }
    }
  }
}

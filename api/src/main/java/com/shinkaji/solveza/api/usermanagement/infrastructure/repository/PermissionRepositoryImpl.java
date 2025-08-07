package com.shinkaji.solveza.api.usermanagement.infrastructure.repository;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.usermanagement.domain.model.Permission;
import com.shinkaji.solveza.api.usermanagement.domain.repository.PermissionRepository;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.PermissionMapper;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.PermissionDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryImpl implements PermissionRepository {

  private final PermissionMapper permissionMapper;

  public PermissionRepositoryImpl(PermissionMapper permissionMapper) {
    this.permissionMapper = permissionMapper;
  }

  @Override
  public Optional<Permission> findById(PermissionId permissionId) {
    return permissionMapper.findById(permissionId.value().toString()).map(this::toDomain);
  }

  @Override
  public Optional<Permission> findByName(String name) {
    return permissionMapper.findByName(name).map(this::toDomain);
  }

  @Override
  public List<Permission> findAll() {
    return permissionMapper.findAll().stream().map(this::toDomain).collect(Collectors.toList());
  }

  @Override
  public void save(Permission permission) {
    PermissionDto permissionDto = toDto(permission);

    if (permissionMapper.existsById(permission.getId().toString())) {
      permissionMapper.update(permissionDto);
    } else {
      permissionMapper.insert(permissionDto);
    }
  }

  @Override
  public void delete(PermissionId permissionId) {
    permissionMapper.delete(permissionId.value().toString());
  }

  @Override
  public boolean existsById(PermissionId permissionId) {
    return permissionMapper.existsById(permissionId.value().toString());
  }

  @Override
  public boolean existsByName(String name) {
    return permissionMapper.existsByName(name);
  }

  private Permission toDomain(PermissionDto dto) {
    return Permission.reconstruct(
        java.util.UUID.fromString(dto.id()),
        dto.createdAt(),
        dto.createdAt(), // Note: Permissions don't have updatedAt in the table
        dto.name(),
        dto.resource(),
        dto.action());
  }

  private PermissionDto toDto(Permission permission) {
    return new PermissionDto(
        permission.getId().toString(),
        permission.getName(),
        permission.getResource(),
        permission.getAction(),
        permission.getCreatedAt());
  }
}

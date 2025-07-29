package com.shinkaji.solveza.api.usermanagement.domain.repository;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.usermanagement.domain.model.Permission;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

  Optional<Permission> findById(PermissionId permissionId);

  Optional<Permission> findByName(String name);

  List<Permission> findAll();

  void save(Permission permission);

  void delete(PermissionId permissionId);

  boolean existsById(PermissionId permissionId);

  boolean existsByName(String name);
}

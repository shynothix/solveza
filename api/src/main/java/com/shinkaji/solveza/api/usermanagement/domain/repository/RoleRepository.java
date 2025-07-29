package com.shinkaji.solveza.api.usermanagement.domain.repository;

import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.usermanagement.domain.model.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {

  Optional<Role> findById(RoleId roleId);

  Optional<Role> findByName(String name);

  List<Role> findAll();

  void save(Role role);

  void delete(RoleId roleId);

  boolean existsById(RoleId roleId);

  boolean existsByName(String name);
}

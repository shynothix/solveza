package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper;

import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.RoleDto;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.RolePermissionDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {

  Optional<RoleDto> findById(@Param("id") String id);

  Optional<RoleDto> findByName(@Param("name") String name);

  List<RoleDto> findAll();

  void insert(@Param("role") RoleDto role);

  void update(@Param("role") RoleDto role);

  void delete(@Param("id") String id);

  boolean existsById(@Param("id") String id);

  boolean existsByName(@Param("name") String name);

  List<RolePermissionDto> findRolePermissionsByRoleId(@Param("roleId") String roleId);

  void insertRolePermission(@Param("rolePermission") RolePermissionDto rolePermission);

  void deleteRolePermission(
      @Param("roleId") String roleId, @Param("permissionId") String permissionId);
}

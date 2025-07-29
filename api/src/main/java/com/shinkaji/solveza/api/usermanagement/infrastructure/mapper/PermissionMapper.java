package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper;

import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.PermissionDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PermissionMapper {

  Optional<PermissionDto> findById(@Param("id") String id);

  Optional<PermissionDto> findByName(@Param("name") String name);

  List<PermissionDto> findAll();

  void insert(@Param("permission") PermissionDto permission);

  void update(@Param("permission") PermissionDto permission);

  void delete(@Param("id") String id);

  boolean existsById(@Param("id") String id);

  boolean existsByName(@Param("name") String name);
}

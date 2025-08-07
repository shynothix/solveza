package com.shinkaji.solveza.api.usermanagement.infrastructure.mapper;

import com.shinkaji.solveza.api.usermanagement.domain.repository.UserSearchCriteria;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.UserDto;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.UserRoleDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

  Optional<UserDto> findById(@Param("id") String id);

  Optional<UserDto> findByProviderAndExternalId(
      @Param("provider") String provider, @Param("externalId") String externalId);

  List<UserDto> findByCriteria(@Param("criteria") UserSearchCriteria criteria);

  void insert(@Param("user") UserDto user);

  void update(@Param("user") UserDto user);

  void delete(@Param("id") String id);

  boolean existsById(@Param("id") String id);

  boolean existsByProviderAndExternalId(
      @Param("provider") String provider, @Param("externalId") String externalId);

  List<UserRoleDto> findUserRolesByUserId(@Param("userId") String userId);

  void insertUserRole(@Param("userRole") UserRoleDto userRole);

  void deleteUserRole(@Param("userId") String userId, @Param("roleId") String roleId);
}

package com.shinkaji.solveza.api.usermanagement.infrastructure.repository;

import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserSearchCriteria;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.UserMapper;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.UserDto;
import com.shinkaji.solveza.api.usermanagement.infrastructure.mapper.dto.UserRoleDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final UserMapper userMapper;

  public UserRepositoryImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public Optional<User> findById(UserId userId) {
    return userMapper.findById(userId.value().toString()).map(this::toDomain);
  }

  @Override
  public Optional<User> findByProviderAndExternalId(Provider provider, String externalId) {
    return userMapper.findByProviderAndExternalId(provider.name(), externalId).map(this::toDomain);
  }

  @Override
  public void save(User user) {
    UserDto userDto = toDto(user);

    if (userMapper.existsById(user.getId().toString())) {
      userMapper.update(userDto);
    } else {
      userMapper.insert(userDto);
    }

    // Handle user roles
    saveUserRoles(user);
  }

  @Override
  public void delete(UserId userId) {
    userMapper.delete(userId.value().toString());
  }

  @Override
  public boolean existsById(UserId userId) {
    return userMapper.existsById(userId.value().toString());
  }

  @Override
  public boolean existsByProviderAndExternalId(Provider provider, String externalId) {
    return userMapper.existsByProviderAndExternalId(provider.name(), externalId);
  }

  @Override
  public List<User> findByCriteria(UserSearchCriteria criteria) {
    return userMapper.findByCriteria(criteria).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  private User toDomain(UserDto dto) {
    Set<RoleId> roleIds =
        userMapper.findUserRolesByUserId(dto.id()).stream()
            .map(userRoleDto -> new RoleId(java.util.UUID.fromString(userRoleDto.roleId())))
            .collect(Collectors.toSet());

    return User.reconstruct(
        java.util.UUID.fromString(dto.id()),
        dto.createdAt(),
        dto.updatedAt(),
        new Provider(dto.provider()),
        dto.externalId(),
        dto.name(),
        dto.email(),
        roleIds);
  }

  private UserDto toDto(User user) {
    return new UserDto(
        user.getId().toString(),
        user.getProvider().name(),
        user.getExternalId(),
        user.getName(),
        user.getEmail(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }

  private void saveUserRoles(User user) {
    // First, get existing roles
    Set<RoleId> existingRoleIds =
        userMapper.findUserRolesByUserId(user.getId().toString()).stream()
            .map(userRoleDto -> new RoleId(java.util.UUID.fromString(userRoleDto.roleId())))
            .collect(Collectors.toSet());

    Set<RoleId> currentRoleIds = user.getRoleIds();

    // Add new roles
    for (RoleId roleId : currentRoleIds) {
      if (!existingRoleIds.contains(roleId)) {
        UserRoleDto userRoleDto =
            new UserRoleDto(
                user.getUserId().value().toString(),
                roleId.value().toString(),
                LocalDateTime.now());
        userMapper.insertUserRole(userRoleDto);
      }
    }

    // Remove deleted roles
    for (RoleId roleId : existingRoleIds) {
      if (!currentRoleIds.contains(roleId)) {
        userMapper.deleteUserRole(user.getUserId().value().toString(), roleId.value().toString());
      }
    }
  }
}

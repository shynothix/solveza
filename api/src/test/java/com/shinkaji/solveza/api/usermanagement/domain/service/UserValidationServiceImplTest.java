package com.shinkaji.solveza.api.usermanagement.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.shared.domain.exception.UserNotFoundException;
import com.shinkaji.solveza.api.usermanagement.domain.repository.PermissionRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserValidationServiceImplのテスト")
class UserValidationServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private PermissionRepository permissionRepository;

  private UserValidationServiceImpl userValidationService;

  @BeforeEach
  void setUp() {
    userValidationService =
        new UserValidationServiceImpl(userRepository, roleRepository, permissionRepository);
  }

  @Test
  @DisplayName("存在しないユーザーの検証でエラーが発生する")
  void validateUserExists_shouldThrowException_whenUserNotExists() {
    // Given
    UserId userId = UserId.generate();
    when(userRepository.existsById(userId)).thenReturn(false);

    // When & Then
    assertThrows(
        UserNotFoundException.class, () -> userValidationService.validateUserExists(userId));
  }

  @Test
  @DisplayName("存在しないロールの検証でエラーが発生する")
  void validateRoleExists_shouldThrowException_whenRoleNotExists() {
    // Given
    RoleId roleId = RoleId.generate();
    when(roleRepository.existsById(roleId)).thenReturn(false);

    // When & Then
    assertThrows(
        IllegalArgumentException.class, () -> userValidationService.validateRoleExists(roleId));
  }

  @Test
  @DisplayName("存在しない権限の検証でエラーが発生する")
  void validatePermissionExists_shouldThrowException_whenPermissionNotExists() {
    // Given
    PermissionId permissionId = PermissionId.generate();
    when(permissionRepository.existsById(permissionId)).thenReturn(false);

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> userValidationService.validatePermissionExists(permissionId));
  }

  @Test
  @DisplayName("既に存在するユーザーの非存在検証でエラーが発生する")
  void validateUserNotExists_shouldThrowException_whenUserExists() {
    // Given
    UserId userId = UserId.generate();
    when(userRepository.existsById(userId)).thenReturn(true);

    // When & Then
    assertThrows(
        IllegalArgumentException.class, () -> userValidationService.validateUserNotExists(userId));
  }
}

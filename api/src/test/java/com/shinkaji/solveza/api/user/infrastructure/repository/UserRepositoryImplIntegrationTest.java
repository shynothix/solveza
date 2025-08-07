package com.shinkaji.solveza.api.user.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.shinkaji.solveza.api.annotation.RepositoryIntegrationTest;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.infrastructure.repository.UserRepositoryImpl;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@RepositoryIntegrationTest
@DisplayName("UserRepositoryImpl Integration Tests")
class UserRepositoryImplIntegrationTest {

  private final UserRepositoryImpl userRepository;

  UserRepositoryImplIntegrationTest(UserRepositoryImpl userRepository) {
    this.userRepository = userRepository;
  }

  private User testUser;
  private Provider testProvider;
  private String testExternalId;

  @BeforeEach
  void setUp() {
    testProvider = new Provider("google");
    testExternalId = "google_test_123";

    testUser = User.create(testProvider, testExternalId, "Test User", "test@example.com");
  }

  @Test
  @DisplayName("ユーザー保存と取得 - 新規作成")
  void save_NewUser_Success() {
    // Act
    userRepository.save(testUser);

    // Assert
    Optional<User> savedUser = userRepository.findById(new UserId(testUser.getId()));
    assertTrue(savedUser.isPresent());
    assertEquals(testUser.getName(), savedUser.get().getName());
    assertEquals(testUser.getEmail(), savedUser.get().getEmail());
    assertEquals(testUser.getProvider().name(), savedUser.get().getProvider().name());
    assertEquals(testUser.getExternalId(), savedUser.get().getExternalId());
  }

  @Test
  @DisplayName("ユーザー保存と取得 - 更新")
  void save_ExistingUser_Update() {
    // Arrange
    userRepository.save(testUser);

    User updatedUser =
        User.reconstruct(
            testUser.getId(),
            testUser.getCreatedAt(),
            LocalDateTime.now(),
            testUser.getProvider(),
            testUser.getExternalId(),
            "Updated Test User",
            "updated@example.com",
            Set.of());

    // Act
    userRepository.save(updatedUser);

    // Assert
    Optional<User> savedUser = userRepository.findById(new UserId(testUser.getId()));
    assertTrue(savedUser.isPresent());
    assertEquals("Updated Test User", savedUser.get().getName());
    assertEquals("updated@example.com", savedUser.get().getEmail());
  }

  @Test
  @DisplayName("プロバイダーと外部IDでユーザー検索")
  void findByProviderAndExternalId_Success() {
    // Arrange
    userRepository.save(testUser);

    // Act
    Optional<User> foundUser =
        userRepository.findByProviderAndExternalId(testProvider, testExternalId);

    // Assert
    assertTrue(foundUser.isPresent());
    assertEquals(testUser.getName(), foundUser.get().getName());
    assertEquals(testUser.getEmail(), foundUser.get().getEmail());
  }

  @Test
  @DisplayName("存在しないユーザーの検索")
  void findById_NotExists() {
    // Act
    Optional<User> foundUser = userRepository.findById(new UserId(UUID.randomUUID()));

    // Assert
    assertFalse(foundUser.isPresent());
  }

  @Test
  @DisplayName("存在しないプロバイダーと外部IDでの検索")
  void findByProviderAndExternalId_NotExists() {
    // Act
    Optional<User> foundUser =
        userRepository.findByProviderAndExternalId(new Provider("github"), "nonexistent_id");

    // Assert
    assertFalse(foundUser.isPresent());
  }

  @Test
  @DisplayName("ユーザー存在確認 - ID")
  void existsById_Success() {
    // Arrange
    userRepository.save(testUser);

    // Act & Assert
    assertTrue(userRepository.existsById(new UserId(testUser.getId())));
    assertFalse(userRepository.existsById(new UserId(UUID.randomUUID())));
  }

  @Test
  @DisplayName("ユーザー存在確認 - プロバイダーと外部ID")
  void existsByProviderAndExternalId_Success() {
    // Arrange
    userRepository.save(testUser);

    // Act & Assert
    assertTrue(userRepository.existsByProviderAndExternalId(testProvider, testExternalId));
    assertFalse(
        userRepository.existsByProviderAndExternalId(new Provider("github"), "nonexistent_id"));
  }

  @Test
  @DisplayName("ユーザー削除")
  void delete_Success() {
    // Arrange
    userRepository.save(testUser);
    assertTrue(userRepository.existsById(new UserId(testUser.getId())));

    // Act
    userRepository.delete(new UserId(testUser.getId()));

    // Assert
    assertFalse(userRepository.existsById(new UserId(testUser.getId())));
  }

  @Test
  @DisplayName("ユーザーとロールの関係保存")
  void save_WithRoles_Success() {
    // Arrange
    RoleId roleId1 = new RoleId(UUID.randomUUID());
    RoleId roleId2 = new RoleId(UUID.randomUUID());

    User userWithRoles =
        User.reconstruct(
            testUser.getId(),
            testUser.getCreatedAt(),
            testUser.getUpdatedAt(),
            testUser.getProvider(),
            testUser.getExternalId(),
            testUser.getName(),
            testUser.getEmail(),
            Set.of(roleId1, roleId2));

    // Act
    userRepository.save(userWithRoles);

    // Assert
    Optional<User> savedUser = userRepository.findById(new UserId(testUser.getId()));
    assertTrue(savedUser.isPresent());
    assertEquals(2, savedUser.get().getRoleIds().size());
    assertTrue(savedUser.get().getRoleIds().contains(roleId1));
    assertTrue(savedUser.get().getRoleIds().contains(roleId2));
  }

  @Test
  @DisplayName("ユーザーロールの更新 - ロール追加")
  void save_AddRoles_Success() {
    // Arrange - First save user without roles
    userRepository.save(testUser);

    // Add roles
    RoleId newRoleId = new RoleId(UUID.randomUUID());
    User userWithNewRole =
        User.reconstruct(
            testUser.getId(),
            testUser.getCreatedAt(),
            LocalDateTime.now(),
            testUser.getProvider(),
            testUser.getExternalId(),
            testUser.getName(),
            testUser.getEmail(),
            Set.of(newRoleId));

    // Act
    userRepository.save(userWithNewRole);

    // Assert
    Optional<User> savedUser = userRepository.findById(new UserId(testUser.getId()));
    assertTrue(savedUser.isPresent());
    assertEquals(1, savedUser.get().getRoleIds().size());
    assertTrue(savedUser.get().getRoleIds().contains(newRoleId));
  }

  @Test
  @DisplayName("ユーザーロールの更新 - ロール削除")
  void save_RemoveRoles_Success() {
    // Arrange - First save user with roles
    RoleId roleId1 = new RoleId(UUID.randomUUID());
    RoleId roleId2 = new RoleId(UUID.randomUUID());

    User userWithRoles =
        User.reconstruct(
            testUser.getId(),
            testUser.getCreatedAt(),
            testUser.getUpdatedAt(),
            testUser.getProvider(),
            testUser.getExternalId(),
            testUser.getName(),
            testUser.getEmail(),
            Set.of(roleId1, roleId2));
    userRepository.save(userWithRoles);

    // Remove one role
    User userWithFewerRoles =
        User.reconstruct(
            testUser.getId(),
            testUser.getCreatedAt(),
            LocalDateTime.now(),
            testUser.getProvider(),
            testUser.getExternalId(),
            testUser.getName(),
            testUser.getEmail(),
            Set.of(roleId1) // Only keep roleId1
            );

    // Act
    userRepository.save(userWithFewerRoles);

    // Assert
    Optional<User> savedUser = userRepository.findById(new UserId(testUser.getId()));
    assertTrue(savedUser.isPresent());
    assertEquals(1, savedUser.get().getRoleIds().size());
    assertTrue(savedUser.get().getRoleIds().contains(roleId1));
    assertFalse(savedUser.get().getRoleIds().contains(roleId2));
  }
}

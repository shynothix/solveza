package com.shinkaji.solveza.api.usermanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Userエンティティのテスト")
class UserTest {

  @Test
  @DisplayName("有効な情報でユーザーを作成できる")
  void createUser_shouldSucceed_whenValidInformation() {
    // Given
    Provider provider = Provider.google();
    String externalId = "test-external-id";
    String name = "テストユーザー";
    String email = "test@example.com";

    // When
    User user = User.create(provider, externalId, name, email);

    // Then
    assertNotNull(user.getUserId());
    assertEquals(provider, user.getProvider());
    assertEquals(externalId, user.getExternalId());
    assertEquals(name, user.getName());
    assertEquals(email, user.getEmail());
    assertTrue(user.getRoleIds().isEmpty());
    assertNotNull(user.getCreatedAt());
    assertNotNull(user.getUpdatedAt());
  }

  @Test
  @DisplayName("nullプロバイダーでユーザー作成時にエラーが発生する")
  void createUser_shouldThrowException_whenProviderIsNull() {
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> User.create(null, "external-id", "name", "email@example.com"));
  }

  @Test
  @DisplayName("null外部IDでユーザー作成時にエラーが発生する")
  void createUser_shouldThrowException_whenExternalIdIsNull() {
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> User.create(Provider.google(), null, "name", "email@example.com"));
  }

  @Test
  @DisplayName("空の名前でユーザー作成時にエラーが発生する")
  void createUser_shouldThrowException_whenNameIsEmpty() {
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> User.create(Provider.google(), "external-id", "", "email@example.com"));
  }

  @Test
  @DisplayName("ユーザー名を更新できる")
  void updateName_shouldSucceed_whenValidName() {
    // Given
    User user = User.create(Provider.google(), "external-id", "元の名前", "email@example.com");
    String newName = "新しい名前";

    // When
    user.updateName(newName);

    // Then
    assertEquals(newName, user.getName());
  }

  @Test
  @DisplayName("ロールを割り当てできる")
  void assignRole_shouldSucceed_whenValidRoleId() {
    // Given
    User user = User.create(Provider.google(), "external-id", "名前", "email@example.com");
    RoleId roleId = RoleId.generate();

    // When
    user.assignRole(roleId);

    // Then
    assertTrue(user.hasRole(roleId));
    assertEquals(1, user.getRoleIds().size());
  }

  @Test
  @DisplayName("ロールを削除できる")
  void removeRole_shouldSucceed_whenRoleExists() {
    // Given
    User user = User.create(Provider.google(), "external-id", "名前", "email@example.com");
    RoleId roleId = RoleId.generate();
    user.assignRole(roleId);

    // When
    user.removeRole(roleId);

    // Then
    assertFalse(user.hasRole(roleId));
    assertTrue(user.getRoleIds().isEmpty());
  }
}

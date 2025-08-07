package com.shinkaji.solveza.api.shared.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserId値オブジェクトのテスト")
class UserIdTest {

  @Test
  @DisplayName("有効なUUIDでUserIdを作成できる")
  void createUserId_shouldSucceed_whenValidUUID() {
    // Given
    UUID uuid = UUID.randomUUID();

    // When
    UserId userId = new UserId(uuid);

    // Then
    assertEquals(uuid, userId.value());
  }

  @Test
  @DisplayName("nullのUUIDでUserIdを作成するとエラーが発生する")
  void createUserId_shouldThrowException_whenNullUUID() {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> new UserId(null));
  }

  @Test
  @DisplayName("新しいUserIdを生成できる")
  void generateUserId_shouldSucceed() {
    // When
    UserId userId = UserId.generate();

    // Then
    assertNotNull(userId.value());
  }

  @Test
  @DisplayName("文字列からUserIdを作成できる")
  void createUserIdFromString_shouldSucceed_whenValidUUIDString() {
    // Given
    String uuidString = UUID.randomUUID().toString();

    // When
    UserId userId = UserId.fromString(uuidString);

    // Then
    assertEquals(uuidString, userId.toString());
  }

  @Test
  @DisplayName("無効な文字列からUserIdを作成するとエラーが発生する")
  void createUserIdFromString_shouldThrowException_whenInvalidString() {
    // Given
    String invalidString = "invalid-uuid";

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> UserId.fromString(invalidString));
  }

  @Test
  @DisplayName("UserIdの文字列表現が正しく取得できる")
  void toString_shouldReturnCorrectString() {
    // Given
    UUID uuid = UUID.randomUUID();
    UserId userId = new UserId(uuid);

    // When
    String result = userId.toString();

    // Then
    assertEquals(uuid.toString(), result);
  }
}

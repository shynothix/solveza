package com.shinkaji.solveza.api.account.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.shinkaji.solveza.api.shared.domain.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Accountエンティティのテスト")
class AccountTest {

  @Test
  @DisplayName("有効な依頼者と支払者でアカウントを作成できる")
  void createAccount_shouldSucceed_whenValidRequesterAndPayer() {
    // Given
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();

    // When
    Account account = Account.create(requesterId, payerId);

    // Then
    assertNotNull(account.getAccountId());
    assertEquals(requesterId, account.getRequester().userId());
    assertEquals(payerId, account.getPayer().userId());
    assertNotNull(account.getCreatedAt());
    assertNotNull(account.getUpdatedAt());
  }

  @Test
  @DisplayName("null依頼者でアカウント作成時にエラーが発生する")
  void createAccount_shouldThrowException_whenRequesterIsNull() {
    // Given
    UserId payerId = UserId.generate();

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> Account.create(null, payerId));
  }

  @Test
  @DisplayName("null支払者でアカウント作成時にエラーが発生する")
  void createAccount_shouldThrowException_whenPayerIsNull() {
    // Given
    UserId requesterId = UserId.generate();

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> Account.create(requesterId, null));
  }

  @Test
  @DisplayName("同じユーザーが依頼者と支払者の場合にエラーが発生する")
  void createAccount_shouldThrowException_whenSameUserAsRequesterAndPayer() {
    // Given
    UserId userId = UserId.generate();

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> Account.create(userId, userId));
  }

  @Test
  @DisplayName("ユーザーが依頼者かどうかを正しく判定できる")
  void isRequester_shouldReturnCorrectResult() {
    // Given
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();
    UserId otherId = UserId.generate();
    Account account = Account.create(requesterId, payerId);

    // When & Then
    assertTrue(account.isRequester(requesterId));
    assertFalse(account.isRequester(payerId));
    assertFalse(account.isRequester(otherId));
  }

  @Test
  @DisplayName("ユーザーが支払者かどうかを正しく判定できる")
  void isPayer_shouldReturnCorrectResult() {
    // Given
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();
    UserId otherId = UserId.generate();
    Account account = Account.create(requesterId, payerId);

    // When & Then
    assertTrue(account.isPayer(payerId));
    assertFalse(account.isPayer(requesterId));
    assertFalse(account.isPayer(otherId));
  }

  @Test
  @DisplayName("ユーザーがアカウント参加者かどうかを正しく判定できる")
  void isParticipant_shouldReturnCorrectResult() {
    // Given
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();
    UserId otherId = UserId.generate();
    Account account = Account.create(requesterId, payerId);

    // When & Then
    assertTrue(account.isParticipant(requesterId));
    assertTrue(account.isParticipant(payerId));
    assertFalse(account.isParticipant(otherId));
  }
}

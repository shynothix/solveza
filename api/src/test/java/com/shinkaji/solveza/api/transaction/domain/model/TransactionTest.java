package com.shinkaji.solveza.api.transaction.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Transactionエンティティのテスト")
class TransactionTest {

  @Test
  @DisplayName("預かり取引を正常に作成できる")
  void createDeposit_shouldCreateTransaction_whenValidData() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
    String description = "テスト預かり";

    // When
    Transaction transaction = Transaction.createDeposit(accountId, amount, description);

    // Then
    assertNotNull(transaction);
    assertEquals(accountId, transaction.getAccountId());
    assertEquals(TransactionType.DEPOSIT, transaction.getTransactionType());
    assertEquals(amount, transaction.getAmount());
    assertEquals(description, transaction.getDescription());
    assertTrue(transaction.isDeposit());
    assertFalse(transaction.isPayment());
    assertNotNull(transaction.getId());
    assertNotNull(transaction.getExecutedAt());
  }

  @Test
  @DisplayName("支払い取引を正常に作成できる")
  void createPayment_shouldCreateTransaction_whenValidData() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount = new Money(BigDecimal.valueOf(500), Currency.getInstance("JPY"));
    String description = "テスト支払い";

    // When
    Transaction transaction = Transaction.createPayment(accountId, amount, description);

    // Then
    assertNotNull(transaction);
    assertEquals(accountId, transaction.getAccountId());
    assertEquals(TransactionType.PAYMENT, transaction.getTransactionType());
    assertEquals(amount, transaction.getAmount());
    assertEquals(description, transaction.getDescription());
    assertTrue(transaction.isPayment());
    assertFalse(transaction.isDeposit());
    assertNotNull(transaction.getId());
    assertNotNull(transaction.getExecutedAt());
  }

  @Test
  @DisplayName("金額がnullの場合例外が発生する")
  void createDeposit_shouldThrowException_whenAmountIsNull() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    String description = "テスト";

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> Transaction.createDeposit(accountId, null, description));
  }

  @Test
  @DisplayName("説明がnullの場合例外が発生する")
  void createDeposit_shouldThrowException_whenDescriptionIsNull() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));

    // When & Then
    assertThrows(
        IllegalArgumentException.class, () -> Transaction.createDeposit(accountId, amount, null));
  }

  @Test
  @DisplayName("説明が空文字の場合例外が発生する")
  void createDeposit_shouldThrowException_whenDescriptionIsEmpty() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));

    // When & Then
    assertThrows(
        IllegalArgumentException.class, () -> Transaction.createDeposit(accountId, amount, ""));
  }

  @Test
  @DisplayName("説明の前後の空白文字が除去される")
  void createDeposit_shouldTrimDescription_whenDescriptionHasWhitespace() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
    String description = "  テスト説明  ";

    // When
    Transaction transaction = Transaction.createDeposit(accountId, amount, description);

    // Then
    assertEquals("テスト説明", transaction.getDescription());
  }
}

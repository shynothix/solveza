package com.shinkaji.solveza.api.transaction.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import com.shinkaji.solveza.api.shared.domain.exception.InvalidTransactionException;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionValidationServiceImplのテスト")
class TransactionValidationServiceImplTest {

  @Mock private AccountRepository accountRepository;

  private TransactionValidationServiceImpl transactionValidationService;

  @BeforeEach
  void setUp() {
    transactionValidationService = new TransactionValidationServiceImpl(accountRepository);
  }

  @Test
  @DisplayName("存在するアカウントの場合は例外が発生しない")
  void validateAccountExists_shouldNotThrowException_whenAccountExists() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    when(accountRepository.existsById(accountId)).thenReturn(true);

    // When & Then
    assertDoesNotThrow(() -> transactionValidationService.validateAccountExists(accountId));
  }

  @Test
  @DisplayName("存在しないアカウントの場合は例外が発生する")
  void validateAccountExists_shouldThrowException_whenAccountNotExists() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    when(accountRepository.existsById(accountId)).thenReturn(false);

    // When & Then
    assertThrows(
        AccountNotFoundException.class,
        () -> transactionValidationService.validateAccountExists(accountId));
  }

  @Test
  @DisplayName("有効な金額の場合は例外が発生しない")
  void validateTransactionAmount_shouldNotThrowException_whenValidAmount() {
    // Given
    Money amount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));

    // When & Then
    assertDoesNotThrow(() -> transactionValidationService.validateTransactionAmount(amount));
  }

  @Test
  @DisplayName("金額がnullの場合は例外が発生する")
  void validateTransactionAmount_shouldThrowException_whenAmountIsNull() {
    // When & Then
    assertThrows(
        InvalidTransactionException.class,
        () -> transactionValidationService.validateTransactionAmount(null));
  }

  @Test
  @DisplayName("金額が0の場合は例外が発生する")
  void validateTransactionAmount_shouldThrowException_whenAmountIsZero() {
    // Given
    Money amount = new Money(BigDecimal.ZERO, Currency.getInstance("JPY"));

    // When & Then
    assertThrows(
        InvalidTransactionException.class,
        () -> transactionValidationService.validateTransactionAmount(amount));
  }

  @Test
  @DisplayName("金額が負の場合は例外が発生する")
  void validateTransactionAmount_shouldThrowException_whenAmountIsNegative() {
    // Given - Moneyクラスの制約により、負の値のMoneyは作成時に例外が発生する
    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> new Money(BigDecimal.valueOf(-100), Currency.getInstance("JPY")));
  }

  @Test
  @DisplayName("有効な取引種別の場合は例外が発生しない")
  void validateTransactionType_shouldNotThrowException_whenValidType() {
    // When & Then
    assertDoesNotThrow(
        () -> transactionValidationService.validateTransactionType(TransactionType.DEPOSIT));
    assertDoesNotThrow(
        () -> transactionValidationService.validateTransactionType(TransactionType.PAYMENT));
  }

  @Test
  @DisplayName("取引種別がnullの場合は例外が発生する")
  void validateTransactionType_shouldThrowException_whenTypeIsNull() {
    // When & Then
    assertThrows(
        InvalidTransactionException.class,
        () -> transactionValidationService.validateTransactionType(null));
  }
}

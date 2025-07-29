package com.shinkaji.solveza.api.transaction.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountBalanceServiceImplのテスト")
class AccountBalanceServiceImplTest {

  @Mock private TransactionRepository transactionRepository;

  private AccountBalanceServiceImpl accountBalanceService;

  @BeforeEach
  void setUp() {
    accountBalanceService = new AccountBalanceServiceImpl(transactionRepository);
  }

  @Test
  @DisplayName("取引がない場合は残高0が返される")
  void calculateBalance_shouldReturnZero_whenNoTransactions() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    when(transactionRepository.findByAccountId(accountId)).thenReturn(List.of());

    // When
    Money balance = accountBalanceService.calculateBalance(accountId);

    // Then
    assertEquals(BigDecimal.ZERO, balance.amount());
    assertEquals(Currency.getInstance("JPY"), balance.currency());
  }

  @Test
  @DisplayName("預かりのみの場合は正の残高が返される")
  void calculateBalance_shouldReturnPositiveBalance_whenOnlyDeposits() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount1 = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
    Money amount2 = new Money(BigDecimal.valueOf(2000), Currency.getInstance("JPY"));

    Transaction deposit1 = Transaction.createDeposit(accountId, amount1, "預かり1");
    Transaction deposit2 = Transaction.createDeposit(accountId, amount2, "預かり2");

    when(transactionRepository.findByAccountId(accountId))
        .thenReturn(Arrays.asList(deposit1, deposit2));

    // When
    Money balance = accountBalanceService.calculateBalance(accountId);

    // Then
    assertEquals(BigDecimal.valueOf(3000), balance.amount());
    assertEquals(Currency.getInstance("JPY"), balance.currency());
  }

  @Test
  @DisplayName("支払いのみの場合は残高0が返される")
  void calculateBalance_shouldReturnZeroBalance_whenOnlyPayments() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount1 = new Money(BigDecimal.valueOf(500), Currency.getInstance("JPY"));
    Money amount2 = new Money(BigDecimal.valueOf(300), Currency.getInstance("JPY"));

    Transaction payment1 = Transaction.createPayment(accountId, amount1, "支払い1");
    Transaction payment2 = Transaction.createPayment(accountId, amount2, "支払い2");

    when(transactionRepository.findByAccountId(accountId))
        .thenReturn(Arrays.asList(payment1, payment2));

    // When
    Money balance = accountBalanceService.calculateBalance(accountId);

    // Then
    // 現在の実装では負の残高はMoneyクラスの制約により0として返される
    assertEquals(BigDecimal.ZERO, balance.amount());
    assertEquals(Currency.getInstance("JPY"), balance.currency());
  }

  @Test
  @DisplayName("預かりと支払いが混在する場合は正しい残高が計算される")
  void calculateBalance_shouldReturnCorrectBalance_whenMixedTransactions() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money depositAmount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
    Money paymentAmount = new Money(BigDecimal.valueOf(300), Currency.getInstance("JPY"));

    Transaction deposit = Transaction.createDeposit(accountId, depositAmount, "預かり");
    Transaction payment = Transaction.createPayment(accountId, paymentAmount, "支払い");

    when(transactionRepository.findByAccountId(accountId))
        .thenReturn(Arrays.asList(deposit, payment));

    // When
    Money balance = accountBalanceService.calculateBalance(accountId);

    // Then
    assertEquals(BigDecimal.valueOf(700), balance.amount());
    assertEquals(Currency.getInstance("JPY"), balance.currency());
  }
}

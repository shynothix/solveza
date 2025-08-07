package com.shinkaji.solveza.api.transaction.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionId;
import com.shinkaji.solveza.api.transaction.infrastructure.mapper.TransactionMapper;
import com.shinkaji.solveza.api.transaction.infrastructure.mapper.dto.TransactionDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit-test")
@DisplayName("TransactionRepositoryImplのテスト")
class TransactionRepositoryImplTest {

  @Mock private TransactionMapper transactionMapper;

  private TransactionRepositoryImpl transactionRepository;

  @BeforeEach
  void setUp() {
    transactionRepository = new TransactionRepositoryImpl(transactionMapper);
  }

  @Test
  @DisplayName("IDで取引を取得できる")
  void findById_shouldReturnTransaction_whenTransactionExists() {
    // Given
    UUID transactionUuid = UUID.randomUUID();
    UUID accountUuid = UUID.randomUUID();
    TransactionId transactionId = new TransactionId(transactionUuid);

    TransactionDto transactionDto =
        new TransactionDto(
            transactionUuid.toString(),
            accountUuid.toString(),
            "DEPOSIT",
            BigDecimal.valueOf(1000),
            "JPY",
            "テスト預かり",
            LocalDateTime.now(),
            LocalDateTime.now());

    when(transactionMapper.findById(transactionUuid.toString()))
        .thenReturn(Optional.of(transactionDto));

    // When
    Optional<Transaction> result = transactionRepository.findById(transactionId);

    // Then
    assertTrue(result.isPresent());
    Transaction transaction = result.get();
    assertEquals(transactionUuid, transaction.getId());
    assertEquals(accountUuid, transaction.getAccountId().value());
    assertEquals("テスト預かり", transaction.getDescription());
  }

  @Test
  @DisplayName("存在しないIDで検索時に空のOptionalを返す")
  void findById_shouldReturnEmpty_whenTransactionNotExists() {
    // Given
    TransactionId transactionId = new TransactionId(UUID.randomUUID());
    when(transactionMapper.findById(transactionId.value().toString())).thenReturn(Optional.empty());

    // When
    Optional<Transaction> result = transactionRepository.findById(transactionId);

    // Then
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("アカウントIDで取引一覧を取得できる")
  void findByAccountId_shouldReturnTransactions_whenTransactionsExist() {
    // Given
    UUID accountUuid = UUID.randomUUID();
    AccountId accountId = new AccountId(accountUuid);

    TransactionDto dto1 =
        new TransactionDto(
            UUID.randomUUID().toString(),
            accountUuid.toString(),
            "DEPOSIT",
            BigDecimal.valueOf(1000),
            "JPY",
            "預かり1",
            LocalDateTime.now(),
            LocalDateTime.now());

    TransactionDto dto2 =
        new TransactionDto(
            UUID.randomUUID().toString(),
            accountUuid.toString(),
            "PAYMENT",
            BigDecimal.valueOf(500),
            "JPY",
            "支払い1",
            LocalDateTime.now(),
            LocalDateTime.now());

    when(transactionMapper.findByAccountId(accountUuid.toString()))
        .thenReturn(Arrays.asList(dto1, dto2));

    // When
    List<Transaction> result = transactionRepository.findByAccountId(accountId);

    // Then
    assertEquals(2, result.size());
    assertEquals("預かり1", result.get(0).getDescription());
    assertEquals("支払い1", result.get(1).getDescription());
  }

  @Test
  @DisplayName("取引を保存できる")
  void save_shouldInsertTransaction() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    Money amount = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
    Transaction transaction = Transaction.createDeposit(accountId, amount, "テスト預かり");

    // When
    transactionRepository.save(transaction);

    // Then
    ArgumentCaptor<TransactionDto> transactionCaptor =
        ArgumentCaptor.forClass(TransactionDto.class);
    verify(transactionMapper).insert(transactionCaptor.capture());

    TransactionDto savedDto = transactionCaptor.getValue();
    assertEquals(transaction.getId().toString(), savedDto.id());
    assertEquals(accountId.value().toString(), savedDto.accountId());
    assertEquals("DEPOSIT", savedDto.transactionType());
    assertEquals(BigDecimal.valueOf(1000), savedDto.amount());
    assertEquals("JPY", savedDto.currency());
    assertEquals("テスト預かり", savedDto.description());
  }

  @Test
  @DisplayName("取引を削除できる")
  void delete_shouldDeleteTransaction() {
    // Given
    TransactionId transactionId = new TransactionId(UUID.randomUUID());

    // When
    transactionRepository.delete(transactionId);

    // Then
    verify(transactionMapper).delete(transactionId.value().toString());
  }

  @Test
  @DisplayName("取引の存在確認ができる")
  void existsById_shouldReturnTrue_whenTransactionExists() {
    // Given
    TransactionId transactionId = new TransactionId(UUID.randomUUID());
    when(transactionMapper.existsById(transactionId.value().toString())).thenReturn(true);

    // When
    boolean result = transactionRepository.existsById(transactionId);

    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("存在しない取引の確認でfalseを返す")
  void existsById_shouldReturnFalse_whenTransactionNotExists() {
    // Given
    TransactionId transactionId = new TransactionId(UUID.randomUUID());
    when(transactionMapper.existsById(transactionId.value().toString())).thenReturn(false);

    // When
    boolean result = transactionRepository.existsById(transactionId);

    // Then
    assertFalse(result);
  }
}

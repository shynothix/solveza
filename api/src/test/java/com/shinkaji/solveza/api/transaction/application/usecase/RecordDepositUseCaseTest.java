package com.shinkaji.solveza.api.transaction.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.transaction.application.command.RecordDepositCommand;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import com.shinkaji.solveza.api.transaction.domain.service.TransactionValidationService;
import com.shinkaji.solveza.api.transaction.presentation.dto.TransactionDto;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecordDepositUseCaseのテスト")
class RecordDepositUseCaseTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private TransactionValidationService transactionValidationService;

  private RecordDepositUseCase recordDepositUseCase;

  @BeforeEach
  void setUp() {
    recordDepositUseCase =
        new RecordDepositUseCase(transactionRepository, transactionValidationService);
  }

  @Test
  @DisplayName("正常に預かり取引を記録できる")
  void execute_shouldRecordDeposit_whenValidCommand() {
    // Given
    UUID accountId = UUID.randomUUID();
    RecordDepositCommand command =
        new RecordDepositCommand(accountId, BigDecimal.valueOf(1000), "JPY", "テスト預かり");

    doNothing().when(transactionValidationService).validateAccountExists(any());
    doNothing().when(transactionValidationService).validateTransactionAmount(any());
    doNothing().when(transactionValidationService).validateTransactionType(any());

    // When
    TransactionDto result = recordDepositUseCase.execute(command);

    // Then
    assertNotNull(result);
    assertEquals(accountId, result.accountId());
    assertEquals("DEPOSIT", result.transactionType());
    assertEquals(BigDecimal.valueOf(1000), result.amount());
    assertEquals("JPY", result.currency());
    assertEquals("テスト預かり", result.description());
    assertNotNull(result.id());
    assertNotNull(result.executedAt());
    assertNotNull(result.createdAt());

    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(transactionCaptor.capture());
    Transaction savedTransaction = transactionCaptor.getValue();
    assertTrue(savedTransaction.isDeposit());
    assertEquals("テスト預かり", savedTransaction.getDescription());
  }

  @Test
  @DisplayName("バリデーション失敗時に例外が発生する")
  void execute_shouldThrowException_whenValidationFails() {
    // Given
    UUID accountId = UUID.randomUUID();
    RecordDepositCommand command =
        new RecordDepositCommand(accountId, BigDecimal.valueOf(1000), "JPY", "テスト預かり");

    doThrow(new RuntimeException("バリデーションエラー"))
        .when(transactionValidationService)
        .validateAccountExists(any());

    // When & Then
    assertThrows(RuntimeException.class, () -> recordDepositUseCase.execute(command));
    verify(transactionRepository, never()).save(any());
  }
}

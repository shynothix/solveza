package com.shinkaji.solveza.api.transaction.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.transaction.application.command.RecordDepositCommand;
import com.shinkaji.solveza.api.transaction.application.command.RecordPaymentCommand;
import com.shinkaji.solveza.api.transaction.application.query.GetAccountBalanceQuery;
import com.shinkaji.solveza.api.transaction.application.query.GetTransactionHistoryQuery;
import com.shinkaji.solveza.api.transaction.application.usecase.GetAccountBalanceUseCase;
import com.shinkaji.solveza.api.transaction.application.usecase.GetTransactionHistoryUseCase;
import com.shinkaji.solveza.api.transaction.application.usecase.RecordDepositUseCase;
import com.shinkaji.solveza.api.transaction.application.usecase.RecordPaymentUseCase;
import com.shinkaji.solveza.api.transaction.presentation.dto.BalanceDto;
import com.shinkaji.solveza.api.transaction.presentation.dto.TransactionDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionControllerのテスト")
class TransactionControllerTest {

  @Mock private RecordDepositUseCase recordDepositUseCase;

  @Mock private RecordPaymentUseCase recordPaymentUseCase;

  @Mock private GetTransactionHistoryUseCase getTransactionHistoryUseCase;

  @Mock private GetAccountBalanceUseCase getAccountBalanceUseCase;

  private TransactionController transactionController;

  @BeforeEach
  void setUp() {
    transactionController =
        new TransactionController(
            recordDepositUseCase,
            recordPaymentUseCase,
            getTransactionHistoryUseCase,
            getAccountBalanceUseCase);
  }

  @Test
  @DisplayName("預かり取引を正常に記録できる")
  void recordDeposit_shouldReturnCreated_whenValidRequest() {
    // Given
    UUID accountId = UUID.randomUUID();
    RecordDepositCommand command =
        new RecordDepositCommand(accountId, BigDecimal.valueOf(1000), "JPY", "テスト預かり");

    UUID transactionId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    TransactionDto transactionDto =
        new TransactionDto(
            transactionId,
            accountId,
            "DEPOSIT",
            BigDecimal.valueOf(1000),
            "JPY",
            "テスト預かり",
            now,
            now);

    when(recordDepositUseCase.execute(any(RecordDepositCommand.class))).thenReturn(transactionDto);

    // When
    ResponseEntity<TransactionDto> response = transactionController.recordDeposit(command);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(transactionId, response.getBody().id());
    assertEquals(accountId, response.getBody().accountId());
    assertEquals("DEPOSIT", response.getBody().transactionType());
    assertEquals(BigDecimal.valueOf(1000), response.getBody().amount());
    assertEquals("JPY", response.getBody().currency());
    assertEquals("テスト預かり", response.getBody().description());
    verify(recordDepositUseCase).execute(any(RecordDepositCommand.class));
  }

  @Test
  @DisplayName("支払い取引を正常に記録できる")
  void recordPayment_shouldReturnCreated_whenValidRequest() {
    // Given
    UUID accountId = UUID.randomUUID();
    RecordPaymentCommand command =
        new RecordPaymentCommand(accountId, BigDecimal.valueOf(500), "JPY", "テスト支払い");

    UUID transactionId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    TransactionDto transactionDto =
        new TransactionDto(
            transactionId,
            accountId,
            "PAYMENT",
            BigDecimal.valueOf(500),
            "JPY",
            "テスト支払い",
            now,
            now);

    when(recordPaymentUseCase.execute(any(RecordPaymentCommand.class))).thenReturn(transactionDto);

    // When
    ResponseEntity<TransactionDto> response = transactionController.recordPayment(command);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(transactionId, response.getBody().id());
    assertEquals(accountId, response.getBody().accountId());
    assertEquals("PAYMENT", response.getBody().transactionType());
    assertEquals(BigDecimal.valueOf(500), response.getBody().amount());
    assertEquals("JPY", response.getBody().currency());
    assertEquals("テスト支払い", response.getBody().description());
    verify(recordPaymentUseCase).execute(any(RecordPaymentCommand.class));
  }

  @Test
  @DisplayName("取引履歴を正常に取得できる")
  void getTransactionHistory_shouldReturnTransactions_whenValidAccountId() {
    // Given
    UUID accountId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    List<TransactionDto> transactions =
        Arrays.asList(
            new TransactionDto(
                UUID.randomUUID(),
                accountId,
                "DEPOSIT",
                BigDecimal.valueOf(1000),
                "JPY",
                "預かり1",
                now,
                now),
            new TransactionDto(
                UUID.randomUUID(),
                accountId,
                "PAYMENT",
                BigDecimal.valueOf(300),
                "JPY",
                "支払い1",
                now,
                now));

    when(getTransactionHistoryUseCase.execute(any(GetTransactionHistoryQuery.class)))
        .thenReturn(transactions);

    // When
    ResponseEntity<List<TransactionDto>> response =
        transactionController.getTransactionHistory(accountId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals("DEPOSIT", response.getBody().get(0).transactionType());
    assertEquals("PAYMENT", response.getBody().get(1).transactionType());
    verify(getTransactionHistoryUseCase).execute(any(GetTransactionHistoryQuery.class));
  }

  @Test
  @DisplayName("アカウント残高を正常に取得できる")
  void getAccountBalance_shouldReturnBalance_whenValidAccountId() {
    // Given
    UUID accountId = UUID.randomUUID();
    BalanceDto balanceDto = new BalanceDto(accountId, BigDecimal.valueOf(700), "JPY");

    when(getAccountBalanceUseCase.execute(any(GetAccountBalanceQuery.class)))
        .thenReturn(balanceDto);

    // When
    ResponseEntity<BalanceDto> response = transactionController.getAccountBalance(accountId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(accountId, response.getBody().accountId());
    assertEquals(BigDecimal.valueOf(700), response.getBody().amount());
    assertEquals("JPY", response.getBody().currency());
    verify(getAccountBalanceUseCase).execute(any(GetAccountBalanceQuery.class));
  }
}

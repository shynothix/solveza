package com.shinkaji.solveza.api.transaction.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.application.query.GetTransactionHistoryQuery;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import com.shinkaji.solveza.api.transaction.domain.service.TransactionValidationService;
import com.shinkaji.solveza.api.transaction.presentation.dto.TransactionDto;
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
@DisplayName("GetTransactionHistoryUseCaseのテスト")
class GetTransactionHistoryUseCaseTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private TransactionValidationService transactionValidationService;

  private GetTransactionHistoryUseCase getTransactionHistoryUseCase;

  @BeforeEach
  void setUp() {
    getTransactionHistoryUseCase =
        new GetTransactionHistoryUseCase(transactionRepository, transactionValidationService);
  }

  @Test
  @DisplayName("取引履歴を正常に取得できる")
  void execute_shouldReturnTransactionHistory_whenValidQuery() {
    // Given
    UUID accountUuid = UUID.randomUUID();
    GetTransactionHistoryQuery query = new GetTransactionHistoryQuery(accountUuid);

    AccountId accountId = new AccountId(accountUuid);
    Money amount1 = new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
    Money amount2 = new Money(BigDecimal.valueOf(500), Currency.getInstance("JPY"));

    Transaction deposit = Transaction.createDeposit(accountId, amount1, "預かり1");
    Transaction payment = Transaction.createPayment(accountId, amount2, "支払い1");

    doNothing().when(transactionValidationService).validateAccountExists(any());
    when(transactionRepository.findByAccountId(any(AccountId.class)))
        .thenReturn(Arrays.asList(deposit, payment));

    // When
    List<TransactionDto> result = getTransactionHistoryUseCase.execute(query);

    // Then
    assertEquals(2, result.size());

    TransactionDto depositDto = result.getFirst();
    assertEquals(accountUuid, depositDto.accountId());
    assertEquals("DEPOSIT", depositDto.transactionType());
    assertEquals(BigDecimal.valueOf(1000), depositDto.amount());
    assertEquals("JPY", depositDto.currency());
    assertEquals("預かり1", depositDto.description());

    TransactionDto paymentDto = result.get(1);
    assertEquals(accountUuid, paymentDto.accountId());
    assertEquals("PAYMENT", paymentDto.transactionType());
    assertEquals(BigDecimal.valueOf(500), paymentDto.amount());
    assertEquals("JPY", paymentDto.currency());
    assertEquals("支払い1", paymentDto.description());

    verify(transactionValidationService).validateAccountExists(any(AccountId.class));
    verify(transactionRepository).findByAccountId(any(AccountId.class));
  }

  @Test
  @DisplayName("アカウント存在確認でエラーが発生した場合例外が発生する")
  void execute_shouldThrowException_whenAccountValidationFails() {
    // Given
    UUID accountId = UUID.randomUUID();
    GetTransactionHistoryQuery query = new GetTransactionHistoryQuery(accountId);

    doThrow(new RuntimeException("アカウントが存在しません"))
        .when(transactionValidationService)
        .validateAccountExists(any());

    // When & Then
    assertThrows(RuntimeException.class, () -> getTransactionHistoryUseCase.execute(query));
    verify(transactionRepository, never()).findByAccountId(any());
  }
}

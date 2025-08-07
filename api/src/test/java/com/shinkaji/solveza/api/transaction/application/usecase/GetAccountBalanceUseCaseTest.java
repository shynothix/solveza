package com.shinkaji.solveza.api.transaction.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.application.query.GetAccountBalanceQuery;
import com.shinkaji.solveza.api.transaction.domain.service.AccountBalanceService;
import com.shinkaji.solveza.api.transaction.domain.service.TransactionValidationService;
import com.shinkaji.solveza.api.transaction.presentation.dto.BalanceDto;
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
@DisplayName("GetAccountBalanceUseCaseのテスト")
class GetAccountBalanceUseCaseTest {

  @Mock private AccountBalanceService accountBalanceService;

  @Mock private TransactionValidationService transactionValidationService;

  private GetAccountBalanceUseCase getAccountBalanceUseCase;

  @BeforeEach
  void setUp() {
    getAccountBalanceUseCase =
        new GetAccountBalanceUseCase(accountBalanceService, transactionValidationService);
  }

  @Test
  @DisplayName("アカウント残高を正常に取得できる")
  void execute_shouldReturnBalance_whenValidQuery() {
    // Given
    UUID accountUuid = UUID.randomUUID();
    GetAccountBalanceQuery query = new GetAccountBalanceQuery(accountUuid);

    Money balance = new Money(BigDecimal.valueOf(1500), Currency.getInstance("JPY"));

    doNothing().when(transactionValidationService).validateAccountExists(any());
    when(accountBalanceService.calculateBalance(any(AccountId.class))).thenReturn(balance);

    // When
    BalanceDto result = getAccountBalanceUseCase.execute(query);

    // Then
    assertNotNull(result);
    assertEquals(accountUuid, result.accountId());
    assertEquals(BigDecimal.valueOf(1500), result.amount());
    assertEquals("JPY", result.currency());

    verify(transactionValidationService).validateAccountExists(any(AccountId.class));
    verify(accountBalanceService).calculateBalance(any(AccountId.class));
  }

  @Test
  @DisplayName("アカウント存在確認でエラーが発生した場合例外が発生する")
  void execute_shouldThrowException_whenAccountValidationFails() {
    // Given
    UUID accountId = UUID.randomUUID();
    GetAccountBalanceQuery query = new GetAccountBalanceQuery(accountId);

    doThrow(new RuntimeException("アカウントが存在しません"))
        .when(transactionValidationService)
        .validateAccountExists(any());

    // When & Then
    assertThrows(RuntimeException.class, () -> getAccountBalanceUseCase.execute(query));
    verify(accountBalanceService, never()).calculateBalance(any());
  }
}

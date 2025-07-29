package com.shinkaji.solveza.api.account.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.shinkaji.solveza.api.account.application.query.GetAccountQuery;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAccountUseCaseのテスト")
class GetAccountUseCaseTest {

  @Mock private AccountRepository accountRepository;

  private GetAccountUseCase getAccountUseCase;

  @BeforeEach
  void setUp() {
    getAccountUseCase = new GetAccountUseCase(accountRepository);
  }

  @Test
  @DisplayName("正常にアカウントを取得できる")
  void execute_shouldReturnAccount_whenAccountExists() {
    // Given
    UUID accountId = UUID.randomUUID();
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();
    GetAccountQuery query = new GetAccountQuery(accountId);

    Account account = Account.create(requesterId, payerId);
    when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.of(account));

    // When
    AccountDto result = getAccountUseCase.execute(query);

    // Then
    assertNotNull(result);
    assertEquals(account.getId(), result.id());
    assertEquals(requesterId.value(), result.requesterId());
    assertEquals(payerId.value(), result.payerId());
  }

  @Test
  @DisplayName("存在しないアカウントの場合例外が発生する")
  void execute_shouldThrowException_whenAccountNotExists() {
    // Given
    UUID accountId = UUID.randomUUID();
    GetAccountQuery query = new GetAccountQuery(accountId);

    when(accountRepository.findById(any(AccountId.class))).thenReturn(Optional.empty());

    // When & Then
    assertThrows(AccountNotFoundException.class, () -> getAccountUseCase.execute(query));
  }
}

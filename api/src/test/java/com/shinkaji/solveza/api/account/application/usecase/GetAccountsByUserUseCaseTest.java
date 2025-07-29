package com.shinkaji.solveza.api.account.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.shinkaji.solveza.api.account.application.query.GetAccountsByUserQuery;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAccountsByUserUseCaseのテスト")
class GetAccountsByUserUseCaseTest {

  @Mock private AccountRepository accountRepository;

  private GetAccountsByUserUseCase getAccountsByUserUseCase;

  @BeforeEach
  void setUp() {
    getAccountsByUserUseCase = new GetAccountsByUserUseCase(accountRepository);
  }

  @Test
  @DisplayName("ユーザーに関連するアカウント一覧を取得できる")
  void execute_shouldReturnAccounts_whenAccountsExist() {
    // Given
    UUID userId = UUID.randomUUID();
    GetAccountsByUserQuery query = new GetAccountsByUserQuery(userId);

    UserId requesterId = new UserId(userId);
    UserId payerId = UserId.generate();
    Account account1 = Account.create(requesterId, payerId);
    Account account2 = Account.create(payerId, requesterId);

    List<Account> accounts = Arrays.asList(account1, account2);
    when(accountRepository.findByUserId(any(UserId.class))).thenReturn(accounts);

    // When
    List<AccountDto> result = getAccountsByUserUseCase.execute(query);

    // Then
    assertEquals(2, result.size());

    AccountDto dto1 = result.getFirst();
    assertEquals(account1.getId(), dto1.id());
    assertEquals(requesterId.value(), dto1.requesterId());
    assertEquals(payerId.value(), dto1.payerId());

    AccountDto dto2 = result.get(1);
    assertEquals(account2.getId(), dto2.id());
    assertEquals(payerId.value(), dto2.requesterId());
    assertEquals(requesterId.value(), dto2.payerId());
  }

  @Test
  @DisplayName("関連アカウントが存在しない場合空のリストを返す")
  void execute_shouldReturnEmptyList_whenNoAccountsExist() {
    // Given
    UUID userId = UUID.randomUUID();
    GetAccountsByUserQuery query = new GetAccountsByUserQuery(userId);

    when(accountRepository.findByUserId(any(UserId.class))).thenReturn(List.of());

    // When
    List<AccountDto> result = getAccountsByUserUseCase.execute(query);

    // Then
    assertTrue(result.isEmpty());
  }
}

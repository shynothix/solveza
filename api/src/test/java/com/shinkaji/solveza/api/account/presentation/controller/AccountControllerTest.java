package com.shinkaji.solveza.api.account.presentation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.account.application.command.CreateAccountCommand;
import com.shinkaji.solveza.api.account.application.command.DeleteAccountCommand;
import com.shinkaji.solveza.api.account.application.query.GetAccountQuery;
import com.shinkaji.solveza.api.account.application.query.GetAccountsByUserQuery;
import com.shinkaji.solveza.api.account.application.usecase.CreateAccountUseCase;
import com.shinkaji.solveza.api.account.application.usecase.DeleteAccountUseCase;
import com.shinkaji.solveza.api.account.application.usecase.GetAccountUseCase;
import com.shinkaji.solveza.api.account.application.usecase.GetAccountsByUserUseCase;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
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
@DisplayName("AccountControllerのテスト")
class AccountControllerTest {

  @Mock private CreateAccountUseCase createAccountUseCase;

  @Mock private DeleteAccountUseCase deleteAccountUseCase;

  @Mock private GetAccountUseCase getAccountUseCase;

  @Mock private GetAccountsByUserUseCase getAccountsByUserUseCase;

  private AccountController accountController;

  @BeforeEach
  void setUp() {
    accountController =
        new AccountController(
            createAccountUseCase,
            deleteAccountUseCase,
            getAccountUseCase,
            getAccountsByUserUseCase);
  }

  @Test
  @DisplayName("アカウントを正常に作成できる")
  void createAccount_shouldReturnCreated_whenValidRequest() {
    // Given
    UUID requesterId = UUID.randomUUID();
    UUID payerId = UUID.randomUUID();
    CreateAccountCommand command = new CreateAccountCommand(requesterId, payerId);

    UUID accountId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    AccountDto accountDto = new AccountDto(accountId, requesterId, payerId, now, now);

    when(createAccountUseCase.execute(any(CreateAccountCommand.class))).thenReturn(accountDto);

    // When
    ResponseEntity<AccountDto> response = accountController.createAccount(command);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(accountId, response.getBody().id());
    assertEquals(requesterId, response.getBody().requesterId());
    assertEquals(payerId, response.getBody().payerId());
    verify(createAccountUseCase).execute(any(CreateAccountCommand.class));
  }

  @Test
  @DisplayName("アカウントを正常に取得できる")
  void getAccount_shouldReturnAccount_whenAccountExists() {
    // Given
    UUID accountId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    UUID payerId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    AccountDto accountDto = new AccountDto(accountId, requesterId, payerId, now, now);

    when(getAccountUseCase.execute(any(GetAccountQuery.class))).thenReturn(accountDto);

    // When
    ResponseEntity<AccountDto> response = accountController.getAccount(accountId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(accountId, response.getBody().id());
    assertEquals(requesterId, response.getBody().requesterId());
    assertEquals(payerId, response.getBody().payerId());
    verify(getAccountUseCase).execute(any(GetAccountQuery.class));
  }

  @Test
  @DisplayName("存在しないアカウントで例外が発生する")
  void getAccount_shouldThrowException_whenAccountNotExists() {
    // Given
    UUID accountId = UUID.randomUUID();
    when(getAccountUseCase.execute(any(GetAccountQuery.class)))
        .thenThrow(
            new AccountNotFoundException(
                new com.shinkaji.solveza.api.shared.domain.AccountId(accountId)));

    // When & Then
    assertThrows(AccountNotFoundException.class, () -> accountController.getAccount(accountId));
    verify(getAccountUseCase).execute(any(GetAccountQuery.class));
  }

  @Test
  @DisplayName("ユーザー関連アカウント一覧を取得できる")
  void getAccountsByUser_shouldReturnAccounts_whenValidUserId() {
    // Given
    UUID userId = UUID.randomUUID();
    UUID accountId1 = UUID.randomUUID();
    UUID accountId2 = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    List<AccountDto> accounts =
        Arrays.asList(
            new AccountDto(accountId1, userId, UUID.randomUUID(), now, now),
            new AccountDto(accountId2, UUID.randomUUID(), userId, now, now));

    when(getAccountsByUserUseCase.execute(any(GetAccountsByUserQuery.class))).thenReturn(accounts);

    // When
    ResponseEntity<List<AccountDto>> response = accountController.getAccountsByUser(userId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals(accountId1, response.getBody().get(0).id());
    assertEquals(accountId2, response.getBody().get(1).id());
    verify(getAccountsByUserUseCase).execute(any(GetAccountsByUserQuery.class));
  }

  @Test
  @DisplayName("アカウントを正常に削除できる")
  void deleteAccount_shouldReturnNoContent_whenAccountExists() {
    // Given
    UUID accountId = UUID.randomUUID();
    doNothing().when(deleteAccountUseCase).execute(any(DeleteAccountCommand.class));

    // When
    ResponseEntity<Void> response = accountController.deleteAccount(accountId);

    // Then
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(deleteAccountUseCase).execute(any(DeleteAccountCommand.class));
  }

  @Test
  @DisplayName("存在しないアカウント削除で例外が発生する")
  void deleteAccount_shouldThrowException_whenAccountNotExists() {
    // Given
    UUID accountId = UUID.randomUUID();
    doThrow(
            new AccountNotFoundException(
                new com.shinkaji.solveza.api.shared.domain.AccountId(accountId)))
        .when(deleteAccountUseCase)
        .execute(any(DeleteAccountCommand.class));

    // When & Then
    assertThrows(AccountNotFoundException.class, () -> accountController.deleteAccount(accountId));
    verify(deleteAccountUseCase).execute(any(DeleteAccountCommand.class));
  }
}

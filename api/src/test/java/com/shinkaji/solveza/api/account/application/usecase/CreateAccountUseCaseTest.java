package com.shinkaji.solveza.api.account.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.account.application.command.CreateAccountCommand;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.domain.service.AccountValidationService;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAccountUseCaseのテスト")
class CreateAccountUseCaseTest {

  @Mock private AccountRepository accountRepository;

  @Mock private AccountValidationService accountValidationService;

  private CreateAccountUseCase createAccountUseCase;

  @BeforeEach
  void setUp() {
    createAccountUseCase = new CreateAccountUseCase(accountRepository, accountValidationService);
  }

  @Test
  @DisplayName("正常にアカウントを作成できる")
  void execute_shouldCreateAccount_whenValidCommand() {
    // Given
    UUID requesterId = UUID.randomUUID();
    UUID payerId = UUID.randomUUID();
    CreateAccountCommand command = new CreateAccountCommand(requesterId, payerId);

    doNothing()
        .when(accountValidationService)
        .validateUsersExist(any(UserId.class), any(UserId.class));
    doNothing()
        .when(accountValidationService)
        .validateUniqueRequesterPayerPair(any(UserId.class), any(UserId.class));

    // When
    AccountDto result = createAccountUseCase.execute(command);

    // Then
    assertNotNull(result);
    assertEquals(requesterId, result.requesterId());
    assertEquals(payerId, result.payerId());
    assertNotNull(result.id());
    assertNotNull(result.createdAt());
    assertNotNull(result.updatedAt());

    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository).save(accountCaptor.capture());
    Account savedAccount = accountCaptor.getValue();
    assertEquals(requesterId, savedAccount.getRequester().userId().value());
    assertEquals(payerId, savedAccount.getPayer().userId().value());
  }

  @Test
  @DisplayName("バリデーション失敗時に例外が発生する")
  void execute_shouldThrowException_whenValidationFails() {
    // Given
    UUID requesterId = UUID.randomUUID();
    UUID payerId = UUID.randomUUID();
    CreateAccountCommand command = new CreateAccountCommand(requesterId, payerId);

    doThrow(new IllegalArgumentException("バリデーションエラー"))
        .when(accountValidationService)
        .validateUsersExist(any(UserId.class), any(UserId.class));

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> createAccountUseCase.execute(command));
    verify(accountRepository, never()).save(any());
  }
}

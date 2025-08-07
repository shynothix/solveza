package com.shinkaji.solveza.api.account.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.infrastructure.mapper.AccountMapper;
import com.shinkaji.solveza.api.account.infrastructure.mapper.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.time.LocalDateTime;
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
@DisplayName("AccountRepositoryImplのテスト")
class AccountRepositoryImplTest {

  @Mock private AccountMapper accountMapper;

  private AccountRepositoryImpl accountRepository;

  @BeforeEach
  void setUp() {
    accountRepository = new AccountRepositoryImpl(accountMapper);
  }

  @Test
  @DisplayName("IDでアカウントを取得できる")
  void findById_shouldReturnAccount_whenAccountExists() {
    // Given
    UUID accountUuid = UUID.randomUUID();
    UUID requesterUuid = UUID.randomUUID();
    UUID payerUuid = UUID.randomUUID();
    AccountId accountId = new AccountId(accountUuid);

    AccountDto accountDto =
        new AccountDto(
            accountUuid.toString(),
            requesterUuid.toString(),
            payerUuid.toString(),
            LocalDateTime.now(),
            LocalDateTime.now());

    when(accountMapper.findById(accountUuid.toString())).thenReturn(Optional.of(accountDto));

    // When
    Optional<Account> result = accountRepository.findById(accountId);

    // Then
    assertTrue(result.isPresent());
    Account account = result.get();
    assertEquals(accountUuid, account.getId());
    assertEquals(requesterUuid, account.getRequester().userId().value());
    assertEquals(payerUuid, account.getPayer().userId().value());
  }

  @Test
  @DisplayName("存在しないIDで検索時に空のOptionalを返す")
  void findById_shouldReturnEmpty_whenAccountNotExists() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    when(accountMapper.findById(accountId.value().toString())).thenReturn(Optional.empty());

    // When
    Optional<Account> result = accountRepository.findById(accountId);

    // Then
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("新規アカウントを保存できる")
  void save_shouldInsertAccount_whenAccountNotExists() {
    // Given
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();
    Account account = Account.create(requesterId, payerId);

    when(accountMapper.existsById(account.getId().toString())).thenReturn(false);

    // When
    accountRepository.save(account);

    // Then
    ArgumentCaptor<AccountDto> accountCaptor = ArgumentCaptor.forClass(AccountDto.class);
    verify(accountMapper).insert(accountCaptor.capture());
    verify(accountMapper, never()).update(any());

    AccountDto savedDto = accountCaptor.getValue();
    assertEquals(account.getId().toString(), savedDto.id());
    assertEquals(requesterId.value().toString(), savedDto.requesterId());
    assertEquals(payerId.value().toString(), savedDto.payerId());
  }

  @Test
  @DisplayName("既存アカウントを更新できる")
  void save_shouldUpdateAccount_whenAccountExists() {
    // Given
    UserId requesterId = UserId.generate();
    UserId payerId = UserId.generate();
    Account account = Account.create(requesterId, payerId);

    when(accountMapper.existsById(account.getId().toString())).thenReturn(true);

    // When
    accountRepository.save(account);

    // Then
    ArgumentCaptor<AccountDto> accountCaptor = ArgumentCaptor.forClass(AccountDto.class);
    verify(accountMapper).update(accountCaptor.capture());
    verify(accountMapper, never()).insert(any());

    AccountDto updatedDto = accountCaptor.getValue();
    assertEquals(account.getId().toString(), updatedDto.id());
  }
}

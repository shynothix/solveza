package com.shinkaji.solveza.api.account.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.annotation.RepositoryIntegrationTest;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@RepositoryIntegrationTest
@DisplayName("AccountRepositoryImpl Integration Tests")
class AccountRepositoryImplIntegrationTest {

  private final AccountRepositoryImpl accountRepository;

  AccountRepositoryImplIntegrationTest(AccountRepositoryImpl accountRepository) {
    this.accountRepository = accountRepository;
  }

  private Account testAccount;
  private UserId testRequesterId;
  private UserId testPayerId;

  @BeforeEach
  void setUp() {
    testRequesterId = new UserId(UUID.randomUUID());
    testPayerId = new UserId(UUID.randomUUID());

    testAccount = Account.create(testRequesterId, testPayerId);
  }

  @Test
  @DisplayName("アカウント保存と取得")
  void save_And_FindById_Success() {
    // Act
    accountRepository.save(testAccount);

    // Assert
    Optional<Account> savedAccount = accountRepository.findById(new AccountId(testAccount.getId()));
    assertTrue(savedAccount.isPresent());
    assertEquals(testAccount.getId(), savedAccount.get().getId());
    assertEquals(testAccount.getRequester().userId(), savedAccount.get().getRequester().userId());
    assertEquals(testAccount.getPayer().userId(), savedAccount.get().getPayer().userId());
  }

  @Test
  @DisplayName("リクエスターIDでアカウント検索")
  void findByRequesterId_Success() {
    // Arrange
    accountRepository.save(testAccount);

    // Create another account with a different requester
    Account anotherAccount = Account.create(new UserId(UUID.randomUUID()), testPayerId);
    accountRepository.save(anotherAccount);

    // Act
    List<Account> accounts = accountRepository.findByRequesterId(testRequesterId);

    // Assert
    assertEquals(1, accounts.size());
    assertEquals(testAccount.getId(), accounts.getFirst().getId());
    assertEquals(testRequesterId, accounts.getFirst().getRequester().userId());
  }

  @Test
  @DisplayName("ペイヤーIDでアカウント検索")
  void findByPayerId_Success() {
    // Arrange
    accountRepository.save(testAccount);

    // Create another account with the same payer
    Account anotherAccount = Account.create(new UserId(UUID.randomUUID()), testPayerId);
    accountRepository.save(anotherAccount);

    // Act
    List<Account> accounts = accountRepository.findByPayerId(testPayerId);

    // Assert
    assertEquals(2, accounts.size());
    assertTrue(accounts.stream().anyMatch(a -> a.getId().equals(testAccount.getId())));
    assertTrue(accounts.stream().anyMatch(a -> a.getId().equals(anotherAccount.getId())));
  }

  @Test
  @DisplayName("存在しないアカウントの検索")
  void findById_NotExists() {
    // Act
    Optional<Account> foundAccount = accountRepository.findById(new AccountId(UUID.randomUUID()));

    // Assert
    assertFalse(foundAccount.isPresent());
  }

  @Test
  @DisplayName("アカウント存在確認")
  void existsById_Success() {
    // Arrange
    accountRepository.save(testAccount);

    // Act & Assert
    assertTrue(accountRepository.existsById(new AccountId(testAccount.getId())));
    assertFalse(accountRepository.existsById(new AccountId(UUID.randomUUID())));
  }

  @Test
  @DisplayName("アカウント削除")
  void delete_Success() {
    // Arrange
    accountRepository.save(testAccount);
    assertTrue(accountRepository.existsById(new AccountId(testAccount.getId())));

    // Act
    accountRepository.delete(new AccountId(testAccount.getId()));

    // Assert
    assertFalse(accountRepository.existsById(new AccountId(testAccount.getId())));
  }

  @Test
  @DisplayName("アカウント更新")
  void save_ExistingAccount_Update() {
    // Arrange - Save original account
    accountRepository.save(testAccount);

    // Create an updated account with the same ID but different timestamps
    Account updatedAccount =
        Account.reconstruct(
            testAccount.getId(),
            testAccount.getCreatedAt(),
            LocalDateTime.now(),
            testAccount.getRequester().userId(),
            testAccount.getPayer().userId());

    // Act
    accountRepository.save(updatedAccount);

    // Assert
    Optional<Account> savedAccount = accountRepository.findById(new AccountId(testAccount.getId()));
    assertTrue(savedAccount.isPresent());
    assertEquals(testAccount.getId(), savedAccount.get().getId());
    assertTrue(savedAccount.get().getUpdatedAt().isAfter(testAccount.getUpdatedAt()));
  }

  @Test
  @DisplayName("複数アカウントの作成と検索")
  void save_MultipleAccounts_Success() {
    // Arrange
    Account account1 = testAccount;
    Account account2 =
        Account.create(
            testRequesterId, // Same requester
            new UserId(UUID.randomUUID()));
    Account account3 =
        Account.create(
            new UserId(UUID.randomUUID()), testPayerId // Same payer
            );

    // Act
    accountRepository.save(account1);
    accountRepository.save(account2);
    accountRepository.save(account3);

    // Assert
    List<Account> requesterAccounts = accountRepository.findByRequesterId(testRequesterId);
    assertEquals(2, requesterAccounts.size());

    List<Account> payerAccounts = accountRepository.findByPayerId(testPayerId);
    assertEquals(2, payerAccounts.size());
  }

  @Test
  @DisplayName("存在しないリクエスターIDでの検索")
  void findByRequesterId_NotExists() {
    // Act
    List<Account> accounts = accountRepository.findByRequesterId(new UserId(UUID.randomUUID()));

    // Assert
    assertTrue(accounts.isEmpty());
  }

  @Test
  @DisplayName("存在しないペイヤーIDでの検索")
  void findByPayerId_NotExists() {
    // Act
    List<Account> accounts = accountRepository.findByPayerId(new UserId(UUID.randomUUID()));

    // Assert
    assertTrue(accounts.isEmpty());
  }

  @Test
  @DisplayName("アカウント参加者確認")
  void isParticipant_Success() {
    // Act & Assert
    assertTrue(testAccount.isParticipant(testRequesterId));
    assertTrue(testAccount.isParticipant(testPayerId));
    assertFalse(testAccount.isParticipant(new UserId(UUID.randomUUID())));
  }

  @Test
  @DisplayName("リクエスター確認")
  void isRequester_Success() {
    // Act & Assert
    assertTrue(testAccount.isRequester(testRequesterId));
    assertFalse(testAccount.isRequester(testPayerId));
    assertFalse(testAccount.isRequester(new UserId(UUID.randomUUID())));
  }

  @Test
  @DisplayName("ペイヤー確認")
  void isPayer_Success() {
    // Act & Assert
    assertTrue(testAccount.isPayer(testPayerId));
    assertFalse(testAccount.isPayer(testRequesterId));
    assertFalse(testAccount.isPayer(new UserId(UUID.randomUUID())));
  }
}

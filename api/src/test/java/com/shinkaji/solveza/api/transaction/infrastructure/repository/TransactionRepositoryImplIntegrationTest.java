package com.shinkaji.solveza.api.transaction.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.shinkaji.solveza.api.annotation.RepositoryIntegrationTest;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionId;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@RepositoryIntegrationTest
@DisplayName("TransactionRepositoryImpl Integration Tests")
class TransactionRepositoryImplIntegrationTest {

  private final TransactionRepositoryImpl transactionRepository;

  TransactionRepositoryImplIntegrationTest(TransactionRepositoryImpl transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  private Transaction testTransaction;
  private AccountId testAccountId;

  @BeforeEach
  void setUp() {
    testAccountId = new AccountId(UUID.randomUUID());
    Money testAmount = new Money(BigDecimal.valueOf(1000.00), Currency.getInstance("JPY"));

    testTransaction = Transaction.createDeposit(testAccountId, testAmount, "Test transaction");
  }

  @Test
  @DisplayName("取引保存と取得")
  void save_And_FindById_Success() {
    // Act
    transactionRepository.save(testTransaction);

    // Assert
    Optional<Transaction> savedTransaction =
        transactionRepository.findById(new TransactionId(testTransaction.getId()));
    assertTrue(savedTransaction.isPresent());
    assertEquals(testTransaction.getId(), savedTransaction.get().getId());
    assertEquals(
        testTransaction.getAccountId().value(), savedTransaction.get().getAccountId().value());
    assertEquals(testTransaction.getTransactionType(), savedTransaction.get().getTransactionType());
    assertEquals(
        0,
        testTransaction
            .getAmount()
            .amount()
            .compareTo(savedTransaction.get().getAmount().amount()));
    assertEquals(
        testTransaction.getAmount().currency(), savedTransaction.get().getAmount().currency());
    assertEquals(testTransaction.getDescription(), savedTransaction.get().getDescription());
  }

  @Test
  @DisplayName("アカウントIDで取引検索")
  void findByAccountId_Success() {
    // Arrange
    transactionRepository.save(testTransaction);

    // Create another transaction for the same account
    Transaction anotherTransaction =
        Transaction.createPayment(
            testAccountId,
            new Money(BigDecimal.valueOf(500.00), Currency.getInstance("JPY")),
            "Another transaction");
    transactionRepository.save(anotherTransaction);

    // Create a transaction for a different account
    Transaction differentAccountTransaction =
        Transaction.createDeposit(
            new AccountId(UUID.randomUUID()),
            new Money(BigDecimal.valueOf(2000.00), Currency.getInstance("USD")),
            "Different account transaction");
    transactionRepository.save(differentAccountTransaction);

    // Act
    List<Transaction> transactions = transactionRepository.findByAccountId(testAccountId);

    // Assert
    assertEquals(2, transactions.size());
    assertTrue(transactions.stream().anyMatch(t -> t.getId().equals(testTransaction.getId())));
    assertTrue(transactions.stream().anyMatch(t -> t.getId().equals(anotherTransaction.getId())));
    assertFalse(
        transactions.stream().anyMatch(t -> t.getId().equals(differentAccountTransaction.getId())));
  }

  @Test
  @DisplayName("存在しない取引の検索")
  void findById_NotExists() {
    // Act
    Optional<Transaction> foundTransaction =
        transactionRepository.findById(new TransactionId(UUID.randomUUID()));

    // Assert
    assertFalse(foundTransaction.isPresent());
  }

  @Test
  @DisplayName("取引存在確認")
  void existsById_Success() {
    // Arrange
    transactionRepository.save(testTransaction);

    // Act & Assert
    assertTrue(transactionRepository.existsById(new TransactionId(testTransaction.getId())));
    assertFalse(transactionRepository.existsById(new TransactionId(UUID.randomUUID())));
  }

  @Test
  @DisplayName("取引削除")
  void delete_Success() {
    // Arrange
    transactionRepository.save(testTransaction);
    assertTrue(transactionRepository.existsById(new TransactionId(testTransaction.getId())));

    // Act
    transactionRepository.delete(new TransactionId(testTransaction.getId()));

    // Assert
    assertFalse(transactionRepository.existsById(new TransactionId(testTransaction.getId())));
  }

  @Test
  @DisplayName("入金取引の保存と取得")
  void save_DepositTransaction_Success() {
    // Arrange
    Transaction depositTransaction =
        Transaction.createDeposit(
            testAccountId,
            new Money(BigDecimal.valueOf(5000.00), Currency.getInstance("JPY")),
            "Salary deposit");

    // Act
    transactionRepository.save(depositTransaction);

    // Assert
    Optional<Transaction> saved =
        transactionRepository.findById(new TransactionId(depositTransaction.getId()));
    assertTrue(saved.isPresent());
    assertEquals(TransactionType.DEPOSIT, saved.get().getTransactionType());
    assertEquals("Salary deposit", saved.get().getDescription());
    assertEquals(0, new BigDecimal("5000.00").compareTo(saved.get().getAmount().amount()));
    assertEquals(Currency.getInstance("JPY"), saved.get().getAmount().currency());
  }

  @Test
  @DisplayName("支払取引の保存と取得")
  void save_PaymentTransaction_Success() {
    // Arrange
    Transaction paymentTransaction =
        Transaction.createPayment(
            testAccountId,
            new Money(BigDecimal.valueOf(3000.00), Currency.getInstance("USD")),
            "Online purchase");

    // Act
    transactionRepository.save(paymentTransaction);

    // Assert
    Optional<Transaction> saved =
        transactionRepository.findById(new TransactionId(paymentTransaction.getId()));
    assertTrue(saved.isPresent());
    assertEquals(TransactionType.PAYMENT, saved.get().getTransactionType());
    assertEquals("Online purchase", saved.get().getDescription());
    assertEquals(0, new BigDecimal("3000.00").compareTo(saved.get().getAmount().amount()));
    assertEquals(Currency.getInstance("USD"), saved.get().getAmount().currency());
  }

  @Test
  @DisplayName("複数通貨での取引保存")
  void save_MultiCurrencyTransactions_Success() {
    // Arrange
    Transaction jpyTransaction =
        Transaction.createDeposit(
            testAccountId,
            new Money(BigDecimal.valueOf(10000), Currency.getInstance("JPY")),
            "JPY transaction");

    Transaction usdTransaction =
        Transaction.createPayment(
            testAccountId,
            new Money(BigDecimal.valueOf(100.50), Currency.getInstance("USD")),
            "USD transaction");

    Transaction eurTransaction =
        Transaction.createDeposit(
            testAccountId,
            new Money(BigDecimal.valueOf(85.75), Currency.getInstance("EUR")),
            "EUR transaction");

    // Act
    transactionRepository.save(jpyTransaction);
    transactionRepository.save(usdTransaction);
    transactionRepository.save(eurTransaction);

    // Assert
    List<Transaction> transactions = transactionRepository.findByAccountId(testAccountId);
    assertEquals(3, transactions.size());

    assertTrue(
        transactions.stream()
            .anyMatch(
                t ->
                    t.getAmount().currency().equals(Currency.getInstance("JPY"))
                        && t.getAmount().amount().compareTo(new BigDecimal("10000")) == 0));
    assertTrue(
        transactions.stream()
            .anyMatch(
                t ->
                    t.getAmount().currency().equals(Currency.getInstance("USD"))
                        && t.getAmount().amount().compareTo(new BigDecimal("100.50")) == 0));
    assertTrue(
        transactions.stream()
            .anyMatch(
                t ->
                    t.getAmount().currency().equals(Currency.getInstance("EUR"))
                        && t.getAmount().amount().compareTo(new BigDecimal("85.75")) == 0));
  }

  @Test
  @DisplayName("存在しないアカウントIDでの取引検索")
  void findByAccountId_NotExists() {
    // Act
    List<Transaction> transactions =
        transactionRepository.findByAccountId(new AccountId(UUID.randomUUID()));

    // Assert
    assertTrue(transactions.isEmpty());
  }

  @Test
  @DisplayName("取引タイプの確認")
  void transactionType_Check() {
    // Arrange
    Transaction depositTransaction =
        Transaction.createDeposit(
            testAccountId,
            new Money(BigDecimal.valueOf(1000), Currency.getInstance("JPY")),
            "Deposit test");

    Transaction paymentTransaction =
        Transaction.createPayment(
            testAccountId,
            new Money(BigDecimal.valueOf(500), Currency.getInstance("JPY")),
            "Payment test");

    // Act
    transactionRepository.save(depositTransaction);
    transactionRepository.save(paymentTransaction);

    // Assert
    List<Transaction> transactions = transactionRepository.findByAccountId(testAccountId);
    assertEquals(2, transactions.size());

    assertTrue(
        transactions.stream()
            .anyMatch(t -> t.isDeposit() && t.getDescription().equals("Deposit test")));
    assertTrue(
        transactions.stream()
            .anyMatch(t -> t.isPayment() && t.getDescription().equals("Payment test")));
  }
}

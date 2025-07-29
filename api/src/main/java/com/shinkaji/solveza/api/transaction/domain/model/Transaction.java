package com.shinkaji.solveza.api.transaction.domain.model;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.BaseEntity;
import com.shinkaji.solveza.api.shared.domain.Money;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Transaction extends BaseEntity {

  private final TransactionId id;
  private final AccountId accountId;
  private final TransactionType transactionType;
  private final Money amount;
  private final String description;
  private final LocalDateTime executedAt;

  private Transaction(
      TransactionId id,
      AccountId accountId,
      TransactionType transactionType,
      Money amount,
      String description,
      LocalDateTime executedAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    super(id.value(), createdAt, updatedAt);
    this.id = id;
    this.accountId = accountId;
    this.transactionType = transactionType;
    this.amount = amount;
    this.description = description;
    this.executedAt = executedAt;
  }

  public static Transaction createDeposit(AccountId accountId, Money amount, String description) {
    validateTransactionData(amount, description);

    LocalDateTime now = LocalDateTime.now();
    return new Transaction(
        TransactionId.generate(),
        accountId,
        TransactionType.DEPOSIT,
        amount,
        description.trim(),
        now,
        now,
        now);
  }

  public static Transaction createPayment(AccountId accountId, Money amount, String description) {
    validateTransactionData(amount, description);

    LocalDateTime now = LocalDateTime.now();
    return new Transaction(
        TransactionId.generate(),
        accountId,
        TransactionType.PAYMENT,
        amount,
        description.trim(),
        now,
        now,
        now);
  }

  public static Transaction reconstruct(
      UUID id,
      AccountId accountId,
      TransactionType transactionType,
      Money amount,
      String description,
      LocalDateTime executedAt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    return new Transaction(
        new TransactionId(id),
        accountId,
        transactionType,
        amount,
        description,
        executedAt,
        createdAt,
        updatedAt);
  }

  private static void validateTransactionData(Money amount, String description) {
    if (amount == null) {
      throw new IllegalArgumentException("金額は必須です");
    }
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("説明は必須です");
    }
  }

  public boolean isDeposit() {
    return this.transactionType == TransactionType.DEPOSIT;
  }

  public boolean isPayment() {
    return this.transactionType == TransactionType.PAYMENT;
  }

  // Lombokによって自動生成されるgetId()をオーバーライドして、TransactionIdのvalueを返す
  public UUID getId() {
    return id.value();
  }
}

package com.shinkaji.solveza.api.transaction.domain.model;

import java.util.UUID;

public record TransactionId(UUID value) {

  public TransactionId {
    if (value == null) {
      throw new IllegalArgumentException("トランザクションIDは必須です");
    }
  }

  public static TransactionId generate() {
    return new TransactionId(UUID.randomUUID());
  }

  @Override
  public String toString() {
    return value.toString();
  }
}

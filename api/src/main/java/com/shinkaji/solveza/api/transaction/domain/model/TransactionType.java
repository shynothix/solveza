package com.shinkaji.solveza.api.transaction.domain.model;

import lombok.Getter;

public enum TransactionType {
  DEPOSIT("預かり"),
  PAYMENT("支払い");

  @Getter private final String displayName;

  TransactionType(String displayName) {
    this.displayName = displayName;
  }
}

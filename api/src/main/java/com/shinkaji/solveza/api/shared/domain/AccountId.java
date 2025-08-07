package com.shinkaji.solveza.api.shared.domain;

import java.util.UUID;

public record AccountId(UUID value) {

  public AccountId {
    if (value == null) {
      throw new IllegalArgumentException("アカウントIDは必須です");
    }
  }

  public static AccountId generate() {
    return new AccountId(UUID.randomUUID());
  }

  public static AccountId fromString(String uuidString) {
    try {
      return new AccountId(UUID.fromString(uuidString));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("無効なUUID文字列です: " + uuidString, e);
    }
  }
}

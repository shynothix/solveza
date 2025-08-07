package com.shinkaji.solveza.api.shared.domain;

import java.util.UUID;

public record UserId(UUID value) {

  public UserId {
    if (value == null) {
      throw new IllegalArgumentException("ユーザーIDは必須です");
    }
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }

  public static UserId fromString(String uuidString) {
    try {
      return new UserId(UUID.fromString(uuidString));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("無効なUUID文字列です: " + uuidString, e);
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}

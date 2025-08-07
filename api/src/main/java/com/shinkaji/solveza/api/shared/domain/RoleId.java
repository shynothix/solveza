package com.shinkaji.solveza.api.shared.domain;

import java.util.UUID;

public record RoleId(UUID value) {

  public RoleId {
    if (value == null) {
      throw new IllegalArgumentException("ロールIDは必須です");
    }
  }

  public static RoleId generate() {
    return new RoleId(UUID.randomUUID());
  }

  public static RoleId fromString(String uuidString) {
    try {
      return new RoleId(UUID.fromString(uuidString));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("無効なUUID文字列です: " + uuidString, e);
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}

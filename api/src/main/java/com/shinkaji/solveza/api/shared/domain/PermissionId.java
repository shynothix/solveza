package com.shinkaji.solveza.api.shared.domain;

import java.util.UUID;

public record PermissionId(UUID value) {

  public PermissionId {
    if (value == null) {
      throw new IllegalArgumentException("権限IDは必須です");
    }
  }

  public static PermissionId generate() {
    return new PermissionId(UUID.randomUUID());
  }

  public static PermissionId fromString(String uuidString) {
    try {
      return new PermissionId(UUID.fromString(uuidString));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("無効なUUID文字列です: " + uuidString, e);
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}

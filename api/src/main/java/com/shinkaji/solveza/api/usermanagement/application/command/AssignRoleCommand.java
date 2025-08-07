package com.shinkaji.solveza.api.usermanagement.application.command;

import java.util.UUID;

public record AssignRoleCommand(UUID userId, UUID roleId) {
  public AssignRoleCommand {
    if (userId == null) {
      throw new IllegalArgumentException("ユーザーIDは必須です");
    }
    if (roleId == null) {
      throw new IllegalArgumentException("ロールIDは必須です");
    }
  }
}

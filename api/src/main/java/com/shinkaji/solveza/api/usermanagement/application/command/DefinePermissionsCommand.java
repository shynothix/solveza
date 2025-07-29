package com.shinkaji.solveza.api.usermanagement.application.command;

import java.util.Set;
import java.util.UUID;

public record DefinePermissionsCommand(UUID roleId, Set<UUID> permissionIds) {
  public DefinePermissionsCommand {
    if (roleId == null) {
      throw new IllegalArgumentException("ロールIDは必須です");
    }
    if (permissionIds == null) {
      throw new IllegalArgumentException("権限IDセットは必須です");
    }
  }
}

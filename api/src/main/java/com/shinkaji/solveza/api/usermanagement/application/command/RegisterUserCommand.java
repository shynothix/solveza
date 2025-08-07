package com.shinkaji.solveza.api.usermanagement.application.command;

public record RegisterUserCommand(String provider, String externalId, String name, String email) {
  public RegisterUserCommand {
    if (provider == null || provider.trim().isEmpty()) {
      throw new IllegalArgumentException("認証プロバイダーは必須です");
    }
    if (externalId == null || externalId.trim().isEmpty()) {
      throw new IllegalArgumentException("外部IDは必須です");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("名前は必須です");
    }
  }
}

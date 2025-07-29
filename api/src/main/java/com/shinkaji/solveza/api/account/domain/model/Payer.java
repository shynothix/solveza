package com.shinkaji.solveza.api.account.domain.model;

import com.shinkaji.solveza.api.shared.domain.UserId;

public record Payer(UserId userId) {

  public Payer {
    if (userId == null) {
      throw new IllegalArgumentException("支払者のユーザーIDは必須です");
    }
  }
}

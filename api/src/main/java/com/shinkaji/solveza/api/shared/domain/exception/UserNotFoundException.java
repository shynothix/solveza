package com.shinkaji.solveza.api.shared.domain.exception;

import com.shinkaji.solveza.api.shared.domain.UserId;

public class UserNotFoundException extends DomainException {

  public UserNotFoundException(UserId userId) {
    super("ユーザーが見つかりません: " + userId);
  }
}

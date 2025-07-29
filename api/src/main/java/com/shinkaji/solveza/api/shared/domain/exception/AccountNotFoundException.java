package com.shinkaji.solveza.api.shared.domain.exception;

import com.shinkaji.solveza.api.shared.domain.AccountId;

public class AccountNotFoundException extends DomainException {

  public AccountNotFoundException(AccountId accountId) {
    super("アカウントが見つかりません: " + accountId);
  }
}

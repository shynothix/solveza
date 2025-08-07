package com.shinkaji.solveza.api.shared.domain.exception;

import com.shinkaji.solveza.api.shared.domain.UserId;

public class DuplicateAccountException extends DomainException {

  public DuplicateAccountException(UserId requesterId, UserId payerId) {
    super("指定された依頼者と支払者の組み合わせは既に存在します: " + requesterId + ", " + payerId);
  }
}

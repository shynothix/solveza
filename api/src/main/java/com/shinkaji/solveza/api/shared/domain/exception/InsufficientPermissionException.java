package com.shinkaji.solveza.api.shared.domain.exception;

public class InsufficientPermissionException extends DomainException {

  public InsufficientPermissionException(String operation) {
    super("操作に必要な権限がありません: " + operation);
  }
}

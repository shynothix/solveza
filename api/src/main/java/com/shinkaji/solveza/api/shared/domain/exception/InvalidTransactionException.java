package com.shinkaji.solveza.api.shared.domain.exception;

public class InvalidTransactionException extends DomainException {

  public InvalidTransactionException(String reason) {
    super("無効な取引です: " + reason);
  }
}

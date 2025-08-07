package com.shinkaji.solveza.api.transaction.domain.service;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;

public interface TransactionValidationService {

  void validateAccountExists(AccountId accountId);

  void validateTransactionAmount(Money amount);

  void validateTransactionType(TransactionType type);
}

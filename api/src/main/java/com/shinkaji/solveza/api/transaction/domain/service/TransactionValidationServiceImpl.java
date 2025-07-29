package com.shinkaji.solveza.api.transaction.domain.service;

import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import com.shinkaji.solveza.api.shared.domain.exception.InvalidTransactionException;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidationServiceImpl implements TransactionValidationService {

  private final AccountRepository accountRepository;

  public TransactionValidationServiceImpl(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void validateAccountExists(AccountId accountId) {
    if (!accountRepository.existsById(accountId)) {
      throw new AccountNotFoundException(accountId);
    }
  }

  @Override
  public void validateTransactionAmount(Money amount) {
    if (amount == null) {
      throw new InvalidTransactionException("金額は必須です");
    }
    if (amount.amount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new InvalidTransactionException("金額は0より大きい値である必要があります");
    }
  }

  @Override
  public void validateTransactionType(TransactionType type) {
    if (type == null) {
      throw new InvalidTransactionException("取引種別は必須です");
    }
  }
}

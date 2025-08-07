package com.shinkaji.solveza.api.transaction.application.usecase;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.application.query.GetAccountBalanceQuery;
import com.shinkaji.solveza.api.transaction.domain.service.AccountBalanceService;
import com.shinkaji.solveza.api.transaction.domain.service.TransactionValidationService;
import com.shinkaji.solveza.api.transaction.presentation.dto.BalanceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAccountBalanceUseCase {

  private final AccountBalanceService accountBalanceService;
  private final TransactionValidationService transactionValidationService;

  public GetAccountBalanceUseCase(
      AccountBalanceService accountBalanceService,
      TransactionValidationService transactionValidationService) {
    this.accountBalanceService = accountBalanceService;
    this.transactionValidationService = transactionValidationService;
  }

  public BalanceDto execute(GetAccountBalanceQuery query) {
    AccountId accountId = new AccountId(query.accountId());

    // アカウント存在確認
    transactionValidationService.validateAccountExists(accountId);

    Money balance = accountBalanceService.calculateBalance(accountId);

    return new BalanceDto(
        accountId.value(), balance.amount(), balance.currency().getCurrencyCode());
  }
}

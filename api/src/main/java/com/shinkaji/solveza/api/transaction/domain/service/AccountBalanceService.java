package com.shinkaji.solveza.api.transaction.domain.service;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;

public interface AccountBalanceService {

  Money calculateBalance(AccountId accountId);
}

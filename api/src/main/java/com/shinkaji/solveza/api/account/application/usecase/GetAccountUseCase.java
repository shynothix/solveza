package com.shinkaji.solveza.api.account.application.usecase;

import com.shinkaji.solveza.api.account.application.query.GetAccountQuery;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAccountUseCase {

  private final AccountRepository accountRepository;

  public GetAccountUseCase(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public AccountDto execute(GetAccountQuery query) {
    AccountId accountId = new AccountId(query.accountId());

    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));

    return new AccountDto(
        account.getId(),
        account.getRequester().userId().value(),
        account.getPayer().userId().value(),
        account.getCreatedAt(),
        account.getUpdatedAt());
  }
}

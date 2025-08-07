package com.shinkaji.solveza.api.account.application.usecase;

import com.shinkaji.solveza.api.account.application.command.DeleteAccountCommand;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteAccountUseCase {

  private final AccountRepository accountRepository;

  public DeleteAccountUseCase(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public void execute(DeleteAccountCommand command) {
    AccountId accountId = new AccountId(command.accountId());

    if (!accountRepository.existsById(accountId)) {
      throw new AccountNotFoundException(accountId);
    }

    accountRepository.delete(accountId);
  }
}

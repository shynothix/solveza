package com.shinkaji.solveza.api.account.application.usecase;

import com.shinkaji.solveza.api.account.application.command.CreateAccountCommand;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.domain.service.AccountValidationService;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateAccountUseCase {

  private final AccountRepository accountRepository;
  private final AccountValidationService accountValidationService;

  public CreateAccountUseCase(
      AccountRepository accountRepository, AccountValidationService accountValidationService) {
    this.accountRepository = accountRepository;
    this.accountValidationService = accountValidationService;
  }

  public AccountDto execute(CreateAccountCommand command) {
    UserId requesterId = new UserId(command.requesterId());
    UserId payerId = new UserId(command.payerId());

    accountValidationService.validateUsersExist(requesterId, payerId);
    accountValidationService.validateUniqueRequesterPayerPair(requesterId, payerId);

    Account account = Account.create(requesterId, payerId);
    accountRepository.save(account);

    return new AccountDto(
        account.getId(),
        account.getRequester().userId().value(),
        account.getPayer().userId().value(),
        account.getCreatedAt(),
        account.getUpdatedAt());
  }
}

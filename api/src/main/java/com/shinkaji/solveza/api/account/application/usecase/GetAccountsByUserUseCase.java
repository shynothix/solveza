package com.shinkaji.solveza.api.account.application.usecase;

import com.shinkaji.solveza.api.account.application.query.GetAccountsByUserQuery;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAccountsByUserUseCase {

  private final AccountRepository accountRepository;

  public GetAccountsByUserUseCase(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public List<AccountDto> execute(GetAccountsByUserQuery query) {
    UserId userId = new UserId(query.userId());

    List<Account> accounts = accountRepository.findByUserId(userId);

    return accounts.stream()
        .map(
            account ->
                new AccountDto(
                    account.getId(),
                    account.getRequester().userId().value(),
                    account.getPayer().userId().value(),
                    account.getCreatedAt(),
                    account.getUpdatedAt()))
        .collect(Collectors.toList());
  }
}

package com.shinkaji.solveza.api.account.domain.service;

import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import com.shinkaji.solveza.api.shared.domain.exception.DuplicateAccountException;
import com.shinkaji.solveza.api.shared.domain.exception.UserNotFoundException;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountValidationServiceImpl implements AccountValidationService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public AccountValidationServiceImpl(
      AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void validateUsersExist(UserId requesterId, UserId payerId) {
    if (!userRepository.existsById(requesterId)) {
      throw new UserNotFoundException(requesterId);
    }
    if (!userRepository.existsById(payerId)) {
      throw new UserNotFoundException(payerId);
    }
  }

  @Override
  public void validateAccountExists(AccountId accountId) {
    if (!accountRepository.existsById(accountId)) {
      throw new AccountNotFoundException(accountId);
    }
  }

  @Override
  public void validateUniqueRequesterPayerPair(UserId requesterId, UserId payerId) {
    if (accountRepository.existsByRequesterIdAndPayerId(requesterId, payerId)) {
      throw new DuplicateAccountException(requesterId, payerId);
    }
  }
}

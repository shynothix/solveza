package com.shinkaji.solveza.api.account.domain.service;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;

public interface AccountValidationService {

  void validateUsersExist(UserId requesterId, UserId payerId);

  void validateAccountExists(AccountId accountId);

  void validateUniqueRequesterPayerPair(UserId requesterId, UserId payerId);
}

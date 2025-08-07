package com.shinkaji.solveza.api.account.domain.repository;

import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

  Optional<Account> findById(AccountId accountId);

  List<Account> findByUserId(UserId userId);

  List<Account> findByRequesterId(UserId requesterId);

  List<Account> findByPayerId(UserId payerId);

  void save(Account account);

  void delete(AccountId accountId);

  boolean existsById(AccountId accountId);

  boolean existsByRequesterIdAndPayerId(UserId requesterId, UserId payerId);
}

package com.shinkaji.solveza.api.transaction.domain.repository;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionId;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

  void save(Transaction transaction);

  Optional<Transaction> findById(TransactionId transactionId);

  List<Transaction> findByAccountId(AccountId accountId);

  void delete(TransactionId transactionId);

  boolean existsById(TransactionId transactionId);
}

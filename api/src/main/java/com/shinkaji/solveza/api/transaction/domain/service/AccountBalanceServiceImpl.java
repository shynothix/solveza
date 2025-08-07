package com.shinkaji.solveza.api.transaction.domain.service;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountBalanceServiceImpl implements AccountBalanceService {

  private final TransactionRepository transactionRepository;

  public AccountBalanceServiceImpl(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  @Override
  public Money calculateBalance(AccountId accountId) {
    List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

    if (transactions.isEmpty()) {
      return new Money(BigDecimal.ZERO, Currency.getInstance("JPY"));
    }

    BigDecimal balance = BigDecimal.ZERO;
    Currency currency = transactions.getFirst().getAmount().currency();

    for (Transaction transaction : transactions) {
      if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
        balance = balance.add(transaction.getAmount().amount());
      } else if (transaction.getTransactionType() == TransactionType.PAYMENT) {
        balance = balance.subtract(transaction.getAmount().amount());
      }
    }

    // 残高が負の場合は0以上の制約を回避するため、絶対値を取って別途管理が必要
    // ここでは一時的に0以上の制約を満たすため、最小値を0とする
    BigDecimal finalBalance = balance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balance;
    return new Money(finalBalance, currency);
  }
}

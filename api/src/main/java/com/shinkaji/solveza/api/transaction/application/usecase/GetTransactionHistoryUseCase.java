package com.shinkaji.solveza.api.transaction.application.usecase;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.transaction.application.query.GetTransactionHistoryQuery;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import com.shinkaji.solveza.api.transaction.domain.service.TransactionValidationService;
import com.shinkaji.solveza.api.transaction.presentation.dto.TransactionDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetTransactionHistoryUseCase {

  private final TransactionRepository transactionRepository;
  private final TransactionValidationService transactionValidationService;

  public GetTransactionHistoryUseCase(
      TransactionRepository transactionRepository,
      TransactionValidationService transactionValidationService) {
    this.transactionRepository = transactionRepository;
    this.transactionValidationService = transactionValidationService;
  }

  public List<TransactionDto> execute(GetTransactionHistoryQuery query) {
    AccountId accountId = new AccountId(query.accountId());

    // アカウント存在確認
    transactionValidationService.validateAccountExists(accountId);

    List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

    return transactions.stream()
        .map(
            transaction ->
                new TransactionDto(
                    transaction.getId(),
                    transaction.getAccountId().value(),
                    transaction.getTransactionType().name(),
                    transaction.getAmount().amount(),
                    transaction.getAmount().currency().getCurrencyCode(),
                    transaction.getDescription(),
                    transaction.getExecutedAt(),
                    transaction.getCreatedAt()))
        .collect(Collectors.toList());
  }
}

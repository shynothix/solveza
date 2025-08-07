package com.shinkaji.solveza.api.transaction.application.usecase;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.application.command.RecordPaymentCommand;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import com.shinkaji.solveza.api.transaction.domain.service.TransactionValidationService;
import com.shinkaji.solveza.api.transaction.presentation.dto.TransactionDto;
import java.util.Currency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecordPaymentUseCase {

  private final TransactionRepository transactionRepository;
  private final TransactionValidationService transactionValidationService;

  public RecordPaymentUseCase(
      TransactionRepository transactionRepository,
      TransactionValidationService transactionValidationService) {
    this.transactionRepository = transactionRepository;
    this.transactionValidationService = transactionValidationService;
  }

  public TransactionDto execute(RecordPaymentCommand command) {
    AccountId accountId = new AccountId(command.accountId());
    Money amount = new Money(command.amount(), Currency.getInstance(command.currency()));

    // バリデーション
    transactionValidationService.validateAccountExists(accountId);
    transactionValidationService.validateTransactionAmount(amount);
    transactionValidationService.validateTransactionType(TransactionType.PAYMENT);

    // 支払い取引を作成・保存
    Transaction transaction = Transaction.createPayment(accountId, amount, command.description());
    transactionRepository.save(transaction);

    return new TransactionDto(
        transaction.getId(),
        transaction.getAccountId().value(),
        transaction.getTransactionType().name(),
        transaction.getAmount().amount(),
        transaction.getAmount().currency().getCurrencyCode(),
        transaction.getDescription(),
        transaction.getExecutedAt(),
        transaction.getCreatedAt());
  }
}

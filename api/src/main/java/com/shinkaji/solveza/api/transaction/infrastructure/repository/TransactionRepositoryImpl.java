package com.shinkaji.solveza.api.transaction.infrastructure.repository;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.Money;
import com.shinkaji.solveza.api.transaction.domain.model.Transaction;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionId;
import com.shinkaji.solveza.api.transaction.domain.model.TransactionType;
import com.shinkaji.solveza.api.transaction.domain.repository.TransactionRepository;
import com.shinkaji.solveza.api.transaction.infrastructure.mapper.TransactionMapper;
import com.shinkaji.solveza.api.transaction.infrastructure.mapper.dto.TransactionDto;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

  private final TransactionMapper transactionMapper;

  public TransactionRepositoryImpl(TransactionMapper transactionMapper) {
    this.transactionMapper = transactionMapper;
  }

  @Override
  public void save(Transaction transaction) {
    TransactionDto transactionDto = toDto(transaction);
    transactionMapper.insert(transactionDto);
  }

  @Override
  public Optional<Transaction> findById(TransactionId transactionId) {
    return transactionMapper.findById(transactionId.value().toString()).map(this::toDomain);
  }

  @Override
  public List<Transaction> findByAccountId(AccountId accountId) {
    return transactionMapper.findByAccountId(accountId.value().toString()).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(TransactionId transactionId) {
    transactionMapper.delete(transactionId.value().toString());
  }

  @Override
  public boolean existsById(TransactionId transactionId) {
    return transactionMapper.existsById(transactionId.value().toString());
  }

  private Transaction toDomain(TransactionDto dto) {
    return Transaction.reconstruct(
        java.util.UUID.fromString(dto.id()),
        new AccountId(java.util.UUID.fromString(dto.accountId())),
        TransactionType.valueOf(dto.transactionType()),
        new Money(dto.amount(), Currency.getInstance(dto.currency())),
        dto.description(),
        dto.executedAt(),
        dto.createdAt(),
        dto.createdAt());
  }

  private TransactionDto toDto(Transaction transaction) {
    return new TransactionDto(
        transaction.getId().toString(),
        transaction.getAccountId().value().toString(),
        transaction.getTransactionType().name(),
        transaction.getAmount().amount(),
        transaction.getAmount().currency().getCurrencyCode(),
        transaction.getDescription(),
        transaction.getExecutedAt(),
        transaction.getCreatedAt());
  }
}

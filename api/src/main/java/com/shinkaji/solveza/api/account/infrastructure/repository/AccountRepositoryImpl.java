package com.shinkaji.solveza.api.account.infrastructure.repository;

import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.account.infrastructure.mapper.AccountMapper;
import com.shinkaji.solveza.api.account.infrastructure.mapper.dto.AccountDto;
import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

  private final AccountMapper accountMapper;

  public AccountRepositoryImpl(AccountMapper accountMapper) {
    this.accountMapper = accountMapper;
  }

  @Override
  public Optional<Account> findById(AccountId accountId) {
    return accountMapper.findById(accountId.value().toString()).map(this::toDomain);
  }

  @Override
  public List<Account> findByUserId(UserId userId) {
    return accountMapper.findByUserId(userId.value().toString()).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Account> findByRequesterId(UserId requesterId) {
    return accountMapper.findByRequesterId(requesterId.value().toString()).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Account> findByPayerId(UserId payerId) {
    return accountMapper.findByPayerId(payerId.value().toString()).stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void save(Account account) {
    AccountDto accountDto = toDto(account);

    if (accountMapper.existsById(account.getId().toString())) {
      accountMapper.update(accountDto);
    } else {
      accountMapper.insert(accountDto);
    }
  }

  @Override
  public void delete(AccountId accountId) {
    accountMapper.delete(accountId.value().toString());
  }

  @Override
  public boolean existsById(AccountId accountId) {
    return accountMapper.existsById(accountId.value().toString());
  }

  @Override
  public boolean existsByRequesterIdAndPayerId(UserId requesterId, UserId payerId) {
    return accountMapper.existsByRequesterIdAndPayerId(
        requesterId.value().toString(), payerId.value().toString());
  }

  private Account toDomain(AccountDto dto) {
    return Account.reconstruct(
        java.util.UUID.fromString(dto.id()),
        dto.createdAt(),
        dto.updatedAt(),
        new UserId(java.util.UUID.fromString(dto.requesterId())),
        new UserId(java.util.UUID.fromString(dto.payerId())));
  }

  private AccountDto toDto(Account account) {
    return new AccountDto(
        account.getId().toString(),
        account.getRequester().userId().value().toString(),
        account.getPayer().userId().value().toString(),
        account.getCreatedAt(),
        account.getUpdatedAt());
  }
}

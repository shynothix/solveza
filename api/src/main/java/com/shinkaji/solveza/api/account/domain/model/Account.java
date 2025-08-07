package com.shinkaji.solveza.api.account.domain.model;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.BaseEntity;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Account extends BaseEntity {

  private final AccountId accountId;
  private final Requester requester;
  private final Payer payer;

  private Account(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      UserId requesterId,
      UserId payerId) {
    super(id, createdAt, updatedAt);
    this.accountId = new AccountId(id);
    this.requester = new Requester(requesterId);
    this.payer = new Payer(payerId);
  }

  public static Account create(UserId requesterId, UserId payerId) {
    if (requesterId == null) {
      throw new IllegalArgumentException("依頼者IDは必須です");
    }
    if (payerId == null) {
      throw new IllegalArgumentException("支払者IDは必須です");
    }
    if (requesterId.equals(payerId)) {
      throw new IllegalArgumentException("依頼者と支払者は異なるユーザーである必要があります");
    }

    return new Account(
        UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), requesterId, payerId);
  }

  public static Account reconstruct(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      UserId requesterId,
      UserId payerId) {
    return new Account(id, createdAt, updatedAt, requesterId, payerId);
  }

  public boolean isRequester(UserId userId) {
    return requester.userId().equals(userId);
  }

  public boolean isPayer(UserId userId) {
    return payer.userId().equals(userId);
  }

  public boolean isParticipant(UserId userId) {
    return isRequester(userId) || isPayer(userId);
  }
}

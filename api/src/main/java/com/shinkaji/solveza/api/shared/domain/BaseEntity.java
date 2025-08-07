package com.shinkaji.solveza.api.shared.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

  @EqualsAndHashCode.Include protected final UUID id;
  protected final LocalDateTime createdAt;
  protected LocalDateTime updatedAt;

  protected BaseEntity(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
    if (id == null) {
      throw new IllegalArgumentException("IDは必須です");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("作成日時は必須です");
    }
    if (updatedAt == null) {
      throw new IllegalArgumentException("更新日時は必須です");
    }

    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  protected void updateTimestamp() {
    this.updatedAt = LocalDateTime.now();
  }
}

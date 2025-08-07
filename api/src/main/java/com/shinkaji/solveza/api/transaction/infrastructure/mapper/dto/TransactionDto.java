package com.shinkaji.solveza.api.transaction.infrastructure.mapper.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
    String id,
    String accountId,
    String transactionType,
    BigDecimal amount,
    String currency,
    String description,
    LocalDateTime executedAt,
    LocalDateTime createdAt) {}

package com.shinkaji.solveza.api.transaction.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDto(
    UUID id,
    UUID accountId,
    String transactionType,
    BigDecimal amount,
    String currency,
    String description,
    LocalDateTime executedAt,
    LocalDateTime createdAt) {}

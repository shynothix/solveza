package com.shinkaji.solveza.api.transaction.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDto(UUID accountId, BigDecimal amount, String currency) {}

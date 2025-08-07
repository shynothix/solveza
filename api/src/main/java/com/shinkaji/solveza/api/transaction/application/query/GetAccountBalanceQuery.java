package com.shinkaji.solveza.api.transaction.application.query;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetAccountBalanceQuery(@NotNull(message = "アカウントIDは必須です") UUID accountId) {}

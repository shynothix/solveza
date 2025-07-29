package com.shinkaji.solveza.api.account.application.query;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetAccountQuery(@NotNull(message = "アカウントIDは必須です") UUID accountId) {}

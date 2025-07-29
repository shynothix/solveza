package com.shinkaji.solveza.api.account.application.query;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetAccountsByUserQuery(@NotNull(message = "ユーザーIDは必須です") UUID userId) {}

package com.shinkaji.solveza.api.account.application.command;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DeleteAccountCommand(@NotNull(message = "アカウントIDは必須です") UUID accountId) {}

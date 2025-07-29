package com.shinkaji.solveza.api.account.application.command;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateAccountCommand(
    @NotNull(message = "依頼者IDは必須です") UUID requesterId,
    @NotNull(message = "支払者IDは必須です") UUID payerId) {}

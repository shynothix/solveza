package com.shinkaji.solveza.api.transaction.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record RecordDepositCommand(
    @NotNull(message = "アカウントIDは必須です") UUID accountId,
    @NotNull(message = "金額は必須です") @Positive(message = "金額は正の値である必要があります") BigDecimal amount,
    @NotBlank(message = "通貨は必須です") String currency,
    @NotBlank(message = "説明は必須です") String description) {}

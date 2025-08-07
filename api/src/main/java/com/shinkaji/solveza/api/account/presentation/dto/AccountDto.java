package com.shinkaji.solveza.api.account.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDto(
    UUID id, UUID requesterId, UUID payerId, LocalDateTime createdAt, LocalDateTime updatedAt) {}

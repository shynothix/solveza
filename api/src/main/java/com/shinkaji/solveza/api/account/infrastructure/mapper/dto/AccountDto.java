package com.shinkaji.solveza.api.account.infrastructure.mapper.dto;

import java.time.LocalDateTime;

public record AccountDto(
    String id,
    String requesterId,
    String payerId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}

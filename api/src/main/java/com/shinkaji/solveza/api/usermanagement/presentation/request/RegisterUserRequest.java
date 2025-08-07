package com.shinkaji.solveza.api.usermanagement.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
    @NotBlank(message = "認証プロバイダーは必須です") String provider,
    @NotBlank(message = "外部IDは必須です") String externalId,
    @NotBlank(message = "名前は必須です") String name,
    @Email(message = "有効なメールアドレスを入力してください") String email) {}

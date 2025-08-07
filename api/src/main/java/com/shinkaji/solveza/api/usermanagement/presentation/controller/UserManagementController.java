package com.shinkaji.solveza.api.usermanagement.presentation.controller;

import com.shinkaji.solveza.api.usermanagement.application.command.AssignRoleCommand;
import com.shinkaji.solveza.api.usermanagement.application.command.RegisterUserCommand;
import com.shinkaji.solveza.api.usermanagement.application.query.GetUsersQuery;
import com.shinkaji.solveza.api.usermanagement.application.usecase.UserManagementUseCase;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.UserDto;
import com.shinkaji.solveza.api.usermanagement.presentation.request.AssignRoleRequest;
import com.shinkaji.solveza.api.usermanagement.presentation.request.RegisterUserRequest;
import com.shinkaji.solveza.api.usermanagement.presentation.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "ユーザー管理API")
public class UserManagementController {

  private final UserManagementUseCase userManagementUseCase;

  public UserManagementController(UserManagementUseCase userManagementUseCase) {
    this.userManagementUseCase = userManagementUseCase;
  }

  @PostMapping
  @Operation(summary = "ユーザー登録・更新", description = "外部認証済みユーザーを登録または更新します")
  public ResponseEntity<UserResponse> registerOrUpdateUser(
      @Valid @RequestBody RegisterUserRequest request) {

    RegisterUserCommand command =
        new RegisterUserCommand(
            request.provider(), request.externalId(), request.name(), request.email());

    UserDto userDto = userManagementUseCase.registerOrUpdateUser(command);
    UserResponse response = toUserResponse(userDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "ユーザー取得", description = "ユーザーIDでユーザー情報を取得します")
  public ResponseEntity<UserResponse> getUserById(
      @Parameter(description = "ユーザーID") @PathVariable UUID userId) {

    UserDto userDto = userManagementUseCase.getUserById(userId);
    UserResponse response = toUserResponse(userDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "ユーザー一覧・検索", description = "全ユーザー取得または認証プロバイダーと外部IDで検索します")
  public ResponseEntity<List<UserResponse>> getUsers(
      @Parameter(description = "認証プロバイダー") @RequestParam Optional<String> provider,
      @Parameter(description = "外部ID") @RequestParam Optional<String> externalId) {

    GetUsersQuery query = new GetUsersQuery(provider.orElse(null), externalId.orElse(null));
    List<UserDto> users = userManagementUseCase.getUsers(query);
    List<UserResponse> responses = users.stream().map(this::toUserResponse).toList();

    return ResponseEntity.ok(responses);
  }

  @PostMapping("/{userId}/roles")
  @Operation(summary = "ロール割り当て", description = "ユーザーにロールを割り当てます")
  public ResponseEntity<Void> assignRole(
      @Parameter(description = "ユーザーID") @PathVariable UUID userId,
      @Valid @RequestBody AssignRoleRequest request) {

    AssignRoleCommand command = new AssignRoleCommand(userId, request.roleId());
    userManagementUseCase.assignRole(command);

    return ResponseEntity.ok().build();
  }

  private UserResponse toUserResponse(UserDto dto) {
    return new UserResponse(
        dto.id(),
        dto.provider(),
        dto.externalId(),
        dto.name(),
        dto.email(),
        dto.roleIds(),
        dto.createdAt(),
        dto.updatedAt());
  }
}

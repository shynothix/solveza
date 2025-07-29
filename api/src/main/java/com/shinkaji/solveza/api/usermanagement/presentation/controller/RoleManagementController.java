package com.shinkaji.solveza.api.usermanagement.presentation.controller;

import com.shinkaji.solveza.api.usermanagement.application.command.DefinePermissionsCommand;
import com.shinkaji.solveza.api.usermanagement.application.usecase.RoleManagementUseCase;
import com.shinkaji.solveza.api.usermanagement.application.usecase.UserManagementUseCase;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.RoleDto;
import com.shinkaji.solveza.api.usermanagement.presentation.request.CreateRoleRequest;
import com.shinkaji.solveza.api.usermanagement.presentation.request.DefinePermissionsRequest;
import com.shinkaji.solveza.api.usermanagement.presentation.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@Tag(name = "Role Management", description = "ロール管理API")
public class RoleManagementController {

  private final RoleManagementUseCase roleManagementUseCase;
  private final UserManagementUseCase userManagementUseCase;

  public RoleManagementController(
      RoleManagementUseCase roleManagementUseCase, UserManagementUseCase userManagementUseCase) {
    this.roleManagementUseCase = roleManagementUseCase;
    this.userManagementUseCase = userManagementUseCase;
  }

  @PostMapping
  @Operation(summary = "ロール作成", description = "新しいロールを作成します")
  public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {

    RoleDto roleDto = roleManagementUseCase.createRole(request.name(), request.description());
    RoleResponse response = toRoleResponse(roleDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{roleId}")
  @Operation(summary = "ロール取得", description = "ロールIDでロール情報を取得します")
  public ResponseEntity<RoleResponse> getRoleById(
      @Parameter(description = "ロールID") @PathVariable UUID roleId) {

    RoleDto roleDto = roleManagementUseCase.getRoleById(roleId);
    RoleResponse response = toRoleResponse(roleDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "全ロール取得", description = "すべてのロールを取得します")
  public ResponseEntity<List<RoleResponse>> getAllRoles() {

    List<RoleDto> roleDtos = roleManagementUseCase.getAllRoles();
    List<RoleResponse> responses = roleDtos.stream().map(this::toRoleResponse).toList();

    return ResponseEntity.ok(responses);
  }

  @GetMapping("/search")
  @Operation(summary = "ロール名検索", description = "ロール名でロールを検索します")
  public ResponseEntity<RoleResponse> getRoleByName(
      @Parameter(description = "ロール名") @RequestParam String name) {

    RoleDto roleDto = roleManagementUseCase.getRoleByName(name);
    RoleResponse response = toRoleResponse(roleDto);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{roleId}/permissions")
  @Operation(summary = "ロール権限定義", description = "ロールに権限を定義します")
  public ResponseEntity<Void> definePermissions(
      @Parameter(description = "ロールID") @PathVariable UUID roleId,
      @Valid @RequestBody DefinePermissionsRequest request) {

    DefinePermissionsCommand command =
        new DefinePermissionsCommand(roleId, request.permissionIds());
    userManagementUseCase.defineRolePermissions(command);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{roleId}")
  @Operation(summary = "ロール削除", description = "ロールを削除します")
  public ResponseEntity<Void> deleteRole(
      @Parameter(description = "ロールID") @PathVariable UUID roleId) {

    roleManagementUseCase.deleteRole(roleId);
    return ResponseEntity.noContent().build();
  }

  private RoleResponse toRoleResponse(RoleDto dto) {
    return new RoleResponse(
        dto.id(), dto.name(), dto.description(), dto.permissionIds(), dto.createdAt());
  }
}

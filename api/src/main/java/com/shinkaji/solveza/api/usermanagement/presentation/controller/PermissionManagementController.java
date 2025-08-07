package com.shinkaji.solveza.api.usermanagement.presentation.controller;

import com.shinkaji.solveza.api.usermanagement.application.usecase.PermissionManagementUseCase;
import com.shinkaji.solveza.api.usermanagement.presentation.dto.PermissionDto;
import com.shinkaji.solveza.api.usermanagement.presentation.request.CreatePermissionRequest;
import com.shinkaji.solveza.api.usermanagement.presentation.response.PermissionResponse;
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
@RequestMapping("/permissions")
@Tag(name = "Permission Management", description = "権限管理API")
public class PermissionManagementController {

  private final PermissionManagementUseCase permissionManagementUseCase;

  public PermissionManagementController(PermissionManagementUseCase permissionManagementUseCase) {
    this.permissionManagementUseCase = permissionManagementUseCase;
  }

  @PostMapping
  @Operation(summary = "権限作成", description = "新しい権限を作成します")
  public ResponseEntity<PermissionResponse> createPermission(
      @Valid @RequestBody CreatePermissionRequest request) {

    PermissionDto permissionDto =
        permissionManagementUseCase.createPermission(
            request.name(), request.resource(), request.action());
    PermissionResponse response = toPermissionResponse(permissionDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{permissionId}")
  @Operation(summary = "権限取得", description = "権限IDで権限情報を取得します")
  public ResponseEntity<PermissionResponse> getPermissionById(
      @Parameter(description = "権限ID") @PathVariable UUID permissionId) {

    PermissionDto permissionDto = permissionManagementUseCase.getPermissionById(permissionId);
    PermissionResponse response = toPermissionResponse(permissionDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(summary = "全権限取得", description = "すべての権限を取得します")
  public ResponseEntity<List<PermissionResponse>> getAllPermissions() {

    List<PermissionDto> permissions = permissionManagementUseCase.getAllPermissions();
    List<PermissionResponse> responses =
        permissions.stream().map(this::toPermissionResponse).toList();

    return ResponseEntity.ok(responses);
  }

  @GetMapping("/search")
  @Operation(summary = "権限名検索", description = "権限名で権限を検索します")
  public ResponseEntity<PermissionResponse> getPermissionByName(
      @Parameter(description = "権限名") @RequestParam String name) {

    PermissionDto permissionDto = permissionManagementUseCase.getPermissionByName(name);
    PermissionResponse response = toPermissionResponse(permissionDto);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{permissionId}")
  @Operation(summary = "権限削除", description = "権限を削除します")
  public ResponseEntity<Void> deletePermission(
      @Parameter(description = "権限ID") @PathVariable UUID permissionId) {

    permissionManagementUseCase.deletePermission(permissionId);
    return ResponseEntity.noContent().build();
  }

  private PermissionResponse toPermissionResponse(PermissionDto dto) {
    return new PermissionResponse(
        dto.id(), dto.name(), dto.resource(), dto.action(), dto.createdAt());
  }
}

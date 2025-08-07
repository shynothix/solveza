package com.shinkaji.solveza.api.usermanagement.domain.model;

import com.shinkaji.solveza.api.shared.domain.BaseEntity;
import com.shinkaji.solveza.api.shared.domain.PermissionId;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Permission extends BaseEntity {

  private final PermissionId permissionId;
  private final String name;
  private final String resource;
  private final String action;

  private Permission(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String name,
      String resource,
      String action) {
    super(id, createdAt, updatedAt);
    this.permissionId = new PermissionId(id);
    this.name = name;
    this.resource = resource;
    this.action = action;
  }

  public static Permission create(String name, String resource, String action) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("権限名は必須です");
    }
    if (resource == null || resource.trim().isEmpty()) {
      throw new IllegalArgumentException("リソースは必須です");
    }
    if (action == null || action.trim().isEmpty()) {
      throw new IllegalArgumentException("アクションは必須です");
    }

    return new Permission(
        UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), name, resource, action);
  }

  public static Permission reconstruct(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String name,
      String resource,
      String action) {
    return new Permission(id, createdAt, updatedAt, name, resource, action);
  }

  public boolean allowsAccess(String requestedResource, String requestedAction) {
    return this.resource.equals(requestedResource) && this.action.equals(requestedAction);
  }
}

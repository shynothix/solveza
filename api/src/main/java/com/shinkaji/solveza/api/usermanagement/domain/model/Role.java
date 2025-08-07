package com.shinkaji.solveza.api.usermanagement.domain.model;

import com.shinkaji.solveza.api.shared.domain.BaseEntity;
import com.shinkaji.solveza.api.shared.domain.PermissionId;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Role extends BaseEntity {

  private final RoleId roleId;
  private String name;
  private String description;
  private final Set<PermissionId> permissionIds;

  private Role(
      UUID id, LocalDateTime createdAt, LocalDateTime updatedAt, String name, String description) {
    super(id, createdAt, updatedAt);
    this.roleId = new RoleId(id);
    this.name = name;
    this.description = description;
    this.permissionIds = new HashSet<>();
  }

  public static Role create(String name, String description) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("ロール名は必須です");
    }

    return new Role(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), name, description);
  }

  public static Role reconstruct(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String name,
      String description,
      Set<PermissionId> permissionIds) {
    Role role = new Role(id, createdAt, updatedAt, name, description);
    if (permissionIds != null) {
      role.permissionIds.addAll(permissionIds);
    }
    return role;
  }

  public void updateName(String newName) {
    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("ロール名は必須です");
    }
    this.name = newName;
    updateTimestamp();
  }

  public void updateDescription(String newDescription) {
    this.description = newDescription;
    updateTimestamp();
  }

  public void grantPermission(PermissionId permissionId) {
    if (permissionId == null) {
      throw new IllegalArgumentException("権限IDは必須です");
    }
    this.permissionIds.add(permissionId);
    updateTimestamp();
  }

  public void revokePermission(PermissionId permissionId) {
    if (permissionId == null) {
      throw new IllegalArgumentException("権限IDは必須です");
    }
    this.permissionIds.remove(permissionId);
    updateTimestamp();
  }

  public boolean hasPermission(PermissionId permissionId) {
    return this.permissionIds.contains(permissionId);
  }

  // Lombokで生成されるgetPermissionIds()をオーバーライドして防御的コピーを返す
  public Set<PermissionId> getPermissionIds() {
    return new HashSet<>(permissionIds);
  }
}

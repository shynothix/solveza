package com.shinkaji.solveza.api.usermanagement.domain.model;

import com.shinkaji.solveza.api.shared.domain.BaseEntity;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.RoleId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User extends BaseEntity {

  private final UserId userId;
  private final Provider provider;
  private final String externalId;
  private String name;
  private String email;
  private final Set<RoleId> roleIds;

  private User(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Provider provider,
      String externalId,
      String name,
      String email) {
    super(id, createdAt, updatedAt);
    this.userId = new UserId(id);
    this.provider = provider;
    this.externalId = externalId;
    this.name = name;
    this.email = email;
    this.roleIds = new HashSet<>();
  }

  public static User create(Provider provider, String externalId, String name, String email) {
    if (provider == null) {
      throw new IllegalArgumentException("認証プロバイダーは必須です");
    }
    if (externalId == null || externalId.trim().isEmpty()) {
      throw new IllegalArgumentException("外部IDは必須です");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("名前は必須です");
    }

    return new User(
        UUID.randomUUID(),
        LocalDateTime.now(),
        LocalDateTime.now(),
        provider,
        externalId,
        name,
        email);
  }

  public static User reconstruct(
      UUID id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Provider provider,
      String externalId,
      String name,
      String email,
      Set<RoleId> roleIds) {
    User user = new User(id, createdAt, updatedAt, provider, externalId, name, email);
    if (roleIds != null) {
      user.roleIds.addAll(roleIds);
    }
    return user;
  }

  public void updateName(String newName) {
    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("名前は必須です");
    }
    this.name = newName;
    updateTimestamp();
  }

  public void updateEmail(String newEmail) {
    this.email = newEmail;
    updateTimestamp();
  }

  public void assignRole(RoleId roleId) {
    if (roleId == null) {
      throw new IllegalArgumentException("ロールIDは必須です");
    }
    this.roleIds.add(roleId);
    updateTimestamp();
  }

  public void removeRole(RoleId roleId) {
    if (roleId == null) {
      throw new IllegalArgumentException("ロールIDは必須です");
    }
    this.roleIds.remove(roleId);
    updateTimestamp();
  }

  public boolean hasRole(RoleId roleId) {
    return this.roleIds.contains(roleId);
  }

  // Lombokで生成されるgetRoleIds()をオーバーライドして防御的コピーを返す
  public Set<RoleId> getRoleIds() {
    return new HashSet<>(roleIds);
  }
}

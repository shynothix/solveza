package com.shinkaji.solveza.api.shared.domain;

public record Provider(String name) {

  public Provider {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("認証プロバイダー名は必須です");
    }
  }

  public static Provider google() {
    return new Provider("GOOGLE");
  }

  public static Provider github() {
    return new Provider("GITHUB");
  }

  public static Provider microsoft() {
    return new Provider("MICROSOFT");
  }

  public static Provider auth0() {
    return new Provider("AUTH0");
  }
}

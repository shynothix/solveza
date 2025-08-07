package com.shinkaji.solveza.api.infrastructure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@DisplayName("Flywayマイグレーションテスト")
class FlywayMigrationTest {

  @Test
  @DisplayName("Flywayマイグレーションファイルが存在する")
  void flywayMigrationFiles_shouldExist() {
    // マイグレーションファイルが存在することを確認
    ClassPathResource v1 =
        new ClassPathResource("db/migration/V1__Create_user_management_tables.sql");
    ClassPathResource v2 =
        new ClassPathResource("db/migration/V2__Create_account_management_tables.sql");
    ClassPathResource v3 =
        new ClassPathResource("db/migration/V3__Create_transaction_management_tables.sql");

    assertTrue(v1.exists(), "V1マイグレーションファイルが存在する");
    assertTrue(v2.exists(), "V2マイグレーションファイルが存在する");
    assertTrue(v3.exists(), "V3マイグレーションファイルが存在する");
  }
}

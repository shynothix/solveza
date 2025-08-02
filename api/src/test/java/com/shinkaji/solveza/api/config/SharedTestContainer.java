package com.shinkaji.solveza.api.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class SharedTestContainer {

  private static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

  static {
    POSTGRES_CONTAINER =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("solveza_shared_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);
    POSTGRES_CONTAINER.start();
  }

  public static PostgreSQLContainer<?> getInstance() {
    return POSTGRES_CONTAINER;
  }
}

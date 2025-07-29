package com.shinkaji.solveza.api.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
@Profile("integration-test")
@MapperScan("com.shinkaji.solveza.api.**.infrastructure.mapper")
public class IntegrationTestConfiguration {

  @Bean
  @Primary
  @SuppressWarnings("resource")
  public static PostgreSQLContainer<?> postgresContainer() {
    PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("solveza_test")
            .withUsername("test")
            .withPassword("test");
    container.start();
    return container;
  }

  @DynamicPropertySource
  @SuppressWarnings("resource")
  static void configureProperties(DynamicPropertyRegistry registry) {
    PostgreSQLContainer<?> container = postgresContainer();
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
  }
}

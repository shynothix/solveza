package com.shinkaji.solveza.api.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestDataSourceConfiguration {

  @Bean
  public static PostgreSQLContainer<?> postgresContainer() {
    PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("solveza_test")
            .withUsername("test")
            .withPassword("test");
    container.start();
    return container;
  }

  @Bean
  @Primary
  public DataSource testDataSource() {
    PostgreSQLContainer<?> container = postgresContainer();
    return DataSourceBuilder.create()
        .url(container.getJdbcUrl())
        .username(container.getUsername())
        .password(container.getPassword())
        .driverClassName("org.postgresql.Driver")
        .build();
  }

  @Bean
  @Primary
  public SqlSessionFactory testSqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);

    // MyBatisマッパーファイルの場所を設定
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/**/*.xml"));

    // MyBatis設定
    org.apache.ibatis.session.Configuration configuration =
        new org.apache.ibatis.session.Configuration();
    configuration.setMapUnderscoreToCamelCase(true);
    configuration.setDefaultFetchSize(100);
    configuration.setDefaultStatementTimeout(30);
    factoryBean.setConfiguration(configuration);

    return factoryBean.getObject();
  }

  @Bean
  @Primary
  public SqlSessionTemplate testSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    PostgreSQLContainer<?> container = postgresContainer();
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("mybatis.mapper-locations", () -> "classpath:mapper/**/*.xml");
    registry.add("mybatis.configuration.map-underscore-to-camel-case", () -> "true");
  }
}

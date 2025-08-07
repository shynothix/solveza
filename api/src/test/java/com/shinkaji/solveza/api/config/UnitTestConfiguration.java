package com.shinkaji.solveza.api.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("unit-test")
@EnableAutoConfiguration(
    exclude = {
      DataSourceAutoConfiguration.class,
      org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class
    })
public class UnitTestConfiguration {}

package com.shinkaji.solveza.api.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("e2e-test")
@EnableAutoConfiguration
public class E2ETestConfiguration {}

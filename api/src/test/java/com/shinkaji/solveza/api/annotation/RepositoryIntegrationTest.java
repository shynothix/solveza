package com.shinkaji.solveza.api.annotation;

import com.shinkaji.solveza.api.config.IntegrationTestConfiguration;
import java.lang.annotation.*;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@ActiveProfiles("integration-test")
@Testcontainers
@Transactional
@DisabledInNativeImage
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(IntegrationTestConfiguration.class)
public @interface RepositoryIntegrationTest {}

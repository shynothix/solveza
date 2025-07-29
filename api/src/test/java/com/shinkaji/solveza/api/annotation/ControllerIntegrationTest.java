package com.shinkaji.solveza.api.annotation;

import com.shinkaji.solveza.api.config.IntegrationTestConfiguration;
import java.lang.annotation.*;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
@DisabledInNativeImage
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Import(IntegrationTestConfiguration.class)
public @interface ControllerIntegrationTest {}

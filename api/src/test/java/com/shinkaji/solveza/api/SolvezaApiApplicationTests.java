package com.shinkaji.solveza.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class SolvezaApiApplicationTests {

  @Test
  void contextLoads() {
    // この基本的なテストは依存関係なしでSpringコンテキストの読み込みをテスト
  }
}

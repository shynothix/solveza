package com.shinkaji.solveza.api.account.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinkaji.solveza.api.account.application.command.CreateAccountCommand;
import com.shinkaji.solveza.api.annotation.ControllerIntegrationTest;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ControllerIntegrationTest
@DisplayName("AccountController Integration Tests")
class AccountControllerIntegrationTest {

  private final WebApplicationContext webApplicationContext;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;

  AccountControllerIntegrationTest(
      WebApplicationContext webApplicationContext,
      ObjectMapper objectMapper,
      UserRepository userRepository) {
    this.webApplicationContext = webApplicationContext;
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
  }

  private MockMvc mockMvc;
  private UUID testRequesterId;
  private UUID testPayerId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // テスト用ユーザーを作成
    User requester =
        User.create(
            new Provider("test-provider"),
            "test-requester-" + System.currentTimeMillis(),
            "Test Requester",
            "requester@test.com");
    userRepository.save(requester);
    testRequesterId = requester.getId();

    User payer =
        User.create(
            new Provider("test-provider"),
            "test-payer-" + System.currentTimeMillis(),
            "Test Payer",
            "payer@test.com");
    userRepository.save(payer);
    testPayerId = payer.getId();
  }

  @Test
  @DisplayName("口座作成 - 正常ケース")
  void createAccount_Success() throws Exception {
    CreateAccountCommand request = new CreateAccountCommand(testRequesterId, testPayerId);

    mockMvc
        .perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.requesterId").exists())
        .andExpect(jsonPath("$.payerId").exists())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("口座取得 - ID指定")
  void getAccountById_Success() throws Exception {
    CreateAccountCommand createRequest = new CreateAccountCommand(testRequesterId, testPayerId);

    String createResponse =
        mockMvc
            .perform(
                post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String accountId = objectMapper.readTree(createResponse).get("id").asText();

    mockMvc
        .perform(get("/accounts/{accountId}", accountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(accountId))
        .andExpect(jsonPath("$.requesterId").exists())
        .andExpect(jsonPath("$.payerId").exists());
  }

  @Test
  @DisplayName("ユーザーの口座一覧取得")
  void getAccountsByUser_Success() throws Exception {
    CreateAccountCommand request1 = new CreateAccountCommand(testRequesterId, testPayerId);

    // 2つ目のアカウント用に別のペイヤーを作成
    User payer2 =
        User.create(
            new Provider("test-provider"),
            "test-payer2-" + System.currentTimeMillis(),
            "Test Payer 2",
            "payer2@test.com");
    userRepository.save(payer2);

    CreateAccountCommand request2 = new CreateAccountCommand(testRequesterId, payer2.getId());

    mockMvc
        .perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/accounts").param("userId", testRequesterId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("口座削除")
  void deleteAccount_Success() throws Exception {
    CreateAccountCommand createRequest = new CreateAccountCommand(testRequesterId, testPayerId);

    String createResponse =
        mockMvc
            .perform(
                post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String accountId = objectMapper.readTree(createResponse).get("id").asText();

    mockMvc.perform(delete("/accounts/{accountId}", accountId)).andExpect(status().isNoContent());

    mockMvc.perform(get("/accounts/{accountId}", accountId)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("バリデーションエラー - 重複アカウント")
  void createAccount_ValidationError_DuplicateAccount() throws Exception {
    // 最初のアカウントを作成
    CreateAccountCommand request = new CreateAccountCommand(testRequesterId, testPayerId);
    mockMvc
        .perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // 同じ組み合わせで再度作成しようとするとエラー
    mockMvc
        .perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("バリデーションエラー - 存在しないユーザー")
  void createAccount_ValidationError_UserNotFound() throws Exception {
    CreateAccountCommand request =
        new CreateAccountCommand(
            UUID.randomUUID(), // 存在しないユーザーID
            testPayerId);

    mockMvc
        .perform(
            post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("存在しない口座の取得エラー")
  void getAccountById_NotFound() throws Exception {
    UUID nonExistentAccountId = UUID.randomUUID();

    mockMvc
        .perform(get("/accounts/{accountId}", nonExistentAccountId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("無効なUUID形式エラー")
  void getAccountById_InvalidUuidFormat() throws Exception {
    mockMvc
        .perform(get("/accounts/{accountId}", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }
}

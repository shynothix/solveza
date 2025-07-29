package com.shinkaji.solveza.api.user.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinkaji.solveza.api.annotation.ControllerIntegrationTest;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.usermanagement.domain.model.Role;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import com.shinkaji.solveza.api.usermanagement.presentation.request.AssignRoleRequest;
import com.shinkaji.solveza.api.usermanagement.presentation.request.RegisterUserRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ControllerIntegrationTest
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

  private final WebApplicationContext webApplicationContext;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  UserControllerIntegrationTest(
      WebApplicationContext webApplicationContext,
      ObjectMapper objectMapper,
      UserRepository userRepository,
      RoleRepository roleRepository) {
    this.webApplicationContext = webApplicationContext;
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  private MockMvc mockMvc;
  private UUID testUserId;
  private UUID testRoleId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // テスト用ユーザーを作成
    User testUser =
        User.create(
            new Provider("test-provider"),
            "test-user-" + System.currentTimeMillis(),
            "Test User",
            "testuser@test.com");
    userRepository.save(testUser);
    testUserId = testUser.getId();

    // テスト用ロールを作成
    Role testRole =
        Role.create(
            "USER_TEST_ROLE_" + System.currentTimeMillis(), "Test Role for User Controller");
    roleRepository.save(testRole);
    testRoleId = testRole.getId();
  }

  @Test
  @DisplayName("ユーザー登録・更新 - 正常ケース")
  void registerOrUpdateUser_Success() throws Exception {
    RegisterUserRequest request =
        new RegisterUserRequest("google", "google_123456", "Test User", "test@example.com");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.provider").value("google"))
        .andExpect(jsonPath("$.externalId").value("google_123456"))
        .andExpect(jsonPath("$.name").value("Test User"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("ユーザー登録 - バリデーションエラー")
  void registerUser_ValidationError() throws Exception {
    RegisterUserRequest request =
        new RegisterUserRequest(
            "", // 空のprovider
            "", // 空のexternalId
            "", // 空のname
            "invalid-email" // 無効なemail
            );

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("ユーザー取得 - ID指定")
  void getUserById_Success() throws Exception {
    mockMvc.perform(get("/users/{userId}", testUserId)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("ユーザー取得 - プロバイダーと外部ID指定")
  void getUserByProviderAndExternalId_Success() throws Exception {
    mockMvc
        .perform(get("/users").param("provider", "google").param("externalId", "google_123456"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("ロール割り当て - 正常ケース")
  void assignRole_Success() throws Exception {
    AssignRoleRequest request = new AssignRoleRequest(testRoleId);

    mockMvc
        .perform(
            post("/users/{userId}/roles", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("存在しないユーザーの取得")
  void getUserById_NotFound() throws Exception {
    UUID nonExistentUserId = UUID.randomUUID();

    mockMvc.perform(get("/users/{userId}", nonExistentUserId)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("無効なユーザーIDフォーマット")
  void getUserById_InvalidFormat() throws Exception {
    mockMvc.perform(get("/users/{userId}", "invalid-uuid")).andExpect(status().isBadRequest());
  }
}

package com.shinkaji.solveza.api.usermanagement.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinkaji.solveza.api.annotation.ControllerIntegrationTest;
import com.shinkaji.solveza.api.usermanagement.domain.model.Role;
import com.shinkaji.solveza.api.usermanagement.domain.repository.RoleRepository;
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
@DisplayName("UserManagementController Integration Tests")
class UserManagementControllerIntegrationTest {

  private final WebApplicationContext webApplicationContext;
  private final ObjectMapper objectMapper;
  private final RoleRepository roleRepository;

  UserManagementControllerIntegrationTest(
      WebApplicationContext webApplicationContext,
      ObjectMapper objectMapper,
      RoleRepository roleRepository) {
    this.webApplicationContext = webApplicationContext;
    this.objectMapper = objectMapper;
    this.roleRepository = roleRepository;
  }

  private MockMvc mockMvc;
  private UUID testRoleId;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // テスト用ロールを作成（ユニークな名前にする）
    Role testRole =
        Role.create("TEST_ROLE_" + System.currentTimeMillis(), "Test Role for Integration Tests");
    roleRepository.save(testRole);
    testRoleId = testRole.getId();
  }

  @Test
  @DisplayName("新規ユーザー登録")
  void registerUser_NewUser_Success() throws Exception {
    RegisterUserRequest request =
        new RegisterUserRequest("google", "new_user_123", "New Test User", "newuser@example.com");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.provider").value("google"))
        .andExpect(jsonPath("$.externalId").value("new_user_123"))
        .andExpect(jsonPath("$.name").value("New Test User"))
        .andExpect(jsonPath("$.email").value("newuser@example.com"))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("既存ユーザーの更新")
  void registerUser_ExistingUser_Update() throws Exception {
    RegisterUserRequest initialRequest =
        new RegisterUserRequest(
            "github", "existing_user_456", "Initial Name", "initial@example.com");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialRequest)))
        .andExpect(status().isCreated());

    RegisterUserRequest updateRequest =
        new RegisterUserRequest(
            "github", "existing_user_456", "Updated Name", "updated@example.com");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Updated Name"))
        .andExpect(jsonPath("$.email").value("updated@example.com"));
  }

  @Test
  @DisplayName("ユーザー取得 - UUID指定")
  void getUserById_Success() throws Exception {
    RegisterUserRequest request =
        new RegisterUserRequest("microsoft", "test_user_789", "Test User", "testuser@example.com");

    String response =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String userId = objectMapper.readTree(response).get("id").asText();

    mockMvc
        .perform(get("/users/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId))
        .andExpect(jsonPath("$.provider").value("microsoft"))
        .andExpect(jsonPath("$.externalId").value("test_user_789"));
  }

  @Test
  @DisplayName("ユーザー検索 - プロバイダーと外部ID指定")
  void getUsers_WithProviderAndExternalId_Success() throws Exception {
    RegisterUserRequest request =
        new RegisterUserRequest(
            "auth0", "provider_user_101", "Provider User", "provider@example.com");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/users").param("provider", "auth0").param("externalId", "provider_user_101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].provider").value("auth0"))
        .andExpect(jsonPath("$[0].externalId").value("provider_user_101"))
        .andExpect(jsonPath("$[0].name").value("Provider User"));
  }

  @Test
  @DisplayName("ユーザー一覧取得 - 全件")
  void getUsers_All_Success() throws Exception {
    // テスト用ユーザーを複数作成
    RegisterUserRequest user1 =
        new RegisterUserRequest("google", "all_user_1", "User 1", "user1@example.com");
    RegisterUserRequest user2 =
        new RegisterUserRequest("github", "all_user_2", "User 2", "user2@example.com");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
  }

  @Test
  @DisplayName("ユーザー検索 - プロバイダーのみ指定")
  void getUsers_WithProviderOnly_Success() throws Exception {
    RegisterUserRequest user1 =
        new RegisterUserRequest(
            "twitter", "provider_only_1", "Twitter User 1", "twitter1@example.com");
    RegisterUserRequest user2 =
        new RegisterUserRequest(
            "twitter", "provider_only_2", "Twitter User 2", "twitter2@example.com");
    RegisterUserRequest user3 =
        new RegisterUserRequest(
            "linkedin", "linkedin_user", "LinkedIn User", "linkedin@example.com");

    // ユーザーを作成
    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user3)))
        .andExpect(status().isCreated());

    // Twitterプロバイダーのユーザーのみ検索
    mockMvc
        .perform(get("/users").param("provider", "twitter"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)))
        .andExpect(
            jsonPath("$[*].provider")
                .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("twitter"))));
  }

  @Test
  @DisplayName("ユーザー検索 - 該当なし")
  void getUsers_NoMatch_EmptyResult() throws Exception {
    mockMvc
        .perform(get("/users").param("provider", "nonexistent").param("externalId", "nonexistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("ロール割り当て")
  void assignRole_Success() throws Exception {
    RegisterUserRequest userRequest =
        new RegisterUserRequest("facebook", "role_user_202", "Role User", "roleuser@example.com");

    String userResponse =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String userId = objectMapper.readTree(userResponse).get("id").asText();

    AssignRoleRequest roleRequest = new AssignRoleRequest(testRoleId);

    mockMvc
        .perform(
            post("/users/{userId}/roles", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("バリデーションエラー - 空のフィールド")
  void registerUser_ValidationError_EmptyFields() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest("", "", "", "");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("バリデーションエラー - 無効なメールアドレス")
  void registerUser_ValidationError_InvalidEmail() throws Exception {
    RegisterUserRequest request =
        new RegisterUserRequest(
            "google", "valid_external_id", "Valid Name", "invalid-email-format");

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("存在しないユーザー取得エラー")
  void getUserById_NotFound() throws Exception {
    UUID nonExistentUserId = UUID.randomUUID();

    mockMvc.perform(get("/users/{userId}", nonExistentUserId)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("無効なUUID形式エラー")
  void getUserById_InvalidUuidFormat() throws Exception {
    mockMvc
        .perform(get("/users/{userId}", "invalid-uuid-format"))
        .andExpect(status().isBadRequest());
  }
}

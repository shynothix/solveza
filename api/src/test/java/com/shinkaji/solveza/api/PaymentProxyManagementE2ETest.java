package com.shinkaji.solveza.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinkaji.solveza.api.account.application.command.CreateAccountCommand;
import com.shinkaji.solveza.api.config.E2ETestConfiguration;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.transaction.application.command.RecordDepositCommand;
import com.shinkaji.solveza.api.transaction.application.command.RecordPaymentCommand;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import com.shinkaji.solveza.api.usermanagement.presentation.request.RegisterUserRequest;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("e2e-test")
@Import(E2ETestConfiguration.class)
@DisabledInNativeImage
@DisplayName("Payment Proxy Management E2E Tests")
class PaymentProxyManagementE2ETest {

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  private MockMvc mockMvc;
  private String testRequesterId;
  private String testPayerId;
  private String testAccountId;

  @BeforeEach
  void setUp() throws Exception {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // テスト用ユーザー作成
    setupTestUsers();

    // テスト用アカウント作成
    setupTestAccount();
  }

  private void setupTestUsers() throws Exception {
    // Requester作成
    RegisterUserRequest requesterRequest =
        new RegisterUserRequest(
            "e2e-provider",
            "e2e-requester-" + System.currentTimeMillis(),
            "E2E Test Requester",
            "requester@e2e.test");

    String requesterResponse =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requesterRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    testRequesterId = objectMapper.readTree(requesterResponse).get("id").asText();

    // Payer作成
    RegisterUserRequest payerRequest =
        new RegisterUserRequest(
            "e2e-provider",
            "e2e-payer-" + System.currentTimeMillis(),
            "E2E Test Payer",
            "payer@e2e.test");

    String payerResponse =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payerRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    testPayerId = objectMapper.readTree(payerResponse).get("id").asText();
  }

  private void setupTestAccount() throws Exception {
    CreateAccountCommand createAccountRequest =
        new CreateAccountCommand(UUID.fromString(testRequesterId), UUID.fromString(testPayerId));

    String accountResponse =
        mockMvc
            .perform(
                post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAccountRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    testAccountId = objectMapper.readTree(accountResponse).get("id").asText();
  }

  @Test
  @DisplayName("決済プロキシ管理 - 完全なワークフロー")
  void paymentProxyManagement_CompleteWorkflow() throws Exception {
    // 1. 初期残高確認（ゼロ残高）
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(testAccountId))
        .andExpect(jsonPath("$.amount").value(0))
        .andExpect(jsonPath("$.currency").value("JPY"));

    // 2. 入金処理（決済プロキシとしての受金）
    RecordDepositCommand depositCommand =
        new RecordDepositCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(50000.00), "JPY", "決済プロキシ経由入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositCommand)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
        .andExpect(jsonPath("$.amount").value(50000.00))
        .andExpect(jsonPath("$.currency").value("JPY"))
        .andExpect(jsonPath("$.description").value("決済プロキシ経由入金"));

    // 3. 残高確認（入金後）
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(50000.00));

    // 4. 支払処理（決済プロキシとしての送金）
    RecordPaymentCommand paymentCommand =
        new RecordPaymentCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(12000.00), "JPY", "決済プロキシ経由支払");

    mockMvc
        .perform(
            post("/transactions/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentCommand)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionType").value("PAYMENT"))
        .andExpect(jsonPath("$.amount").value(12000.00))
        .andExpect(jsonPath("$.currency").value("JPY"))
        .andExpect(jsonPath("$.description").value("決済プロキシ経由支払"));

    // 5. 最終残高確認
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(38000.00));

    // 6. 取引履歴確認
    mockMvc
        .perform(get("/transactions/history").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
            jsonPath("$[*].transactionType")
                .value(org.hamcrest.Matchers.containsInAnyOrder("DEPOSIT", "PAYMENT")));
  }

  @Test
  @DisplayName("決済プロキシ管理 - 複数通貨対応ワークフロー")
  void paymentProxyManagement_MultiCurrencyWorkflow() throws Exception {
    // JPY入金
    RecordDepositCommand jpyDeposit =
        new RecordDepositCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(100000.00), "JPY", "JPY決済プロキシ入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jpyDeposit)))
        .andExpect(status().isCreated());

    // USD入金
    RecordDepositCommand usdDeposit =
        new RecordDepositCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(500.00), "USD", "USD決済プロキシ入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usdDeposit)))
        .andExpect(status().isCreated());

    // EUR支払
    RecordPaymentCommand eurPayment =
        new RecordPaymentCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(200.00), "EUR", "EUR決済プロキシ支払");

    mockMvc
        .perform(
            post("/transactions/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eurPayment)))
        .andExpect(status().isCreated());

    // 取引履歴確認（複数通貨）
    mockMvc
        .perform(get("/transactions/history").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  @DisplayName("決済プロキシ管理 - エラーハンドリング")
  void paymentProxyManagement_ErrorHandling() throws Exception {
    // 無効な金額での入金試行
    RecordDepositCommand invalidDepositCommand =
        new RecordDepositCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(-1000.00), "JPY", "無効な入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDepositCommand)))
        .andExpect(status().isBadRequest());

    // ゼロ金額での支払試行
    RecordPaymentCommand invalidPaymentCommand =
        new RecordPaymentCommand(UUID.fromString(testAccountId), BigDecimal.ZERO, "JPY", "無効な支払");

    mockMvc
        .perform(
            post("/transactions/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPaymentCommand)))
        .andExpect(status().isBadRequest());

    // 存在しないアカウントでの残高照会
    mockMvc
        .perform(get("/transactions/balance").param("accountId", UUID.randomUUID().toString()))
        .andExpect(status().isNotFound());

    // 無効なUUID形式での取引履歴照会
    mockMvc
        .perform(get("/transactions/history").param("accountId", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("決済プロキシ管理 - 高額取引ワークフロー")
  void paymentProxyManagement_HighValueTransactions() throws Exception {
    // 高額入金
    RecordDepositCommand highValueDeposit =
        new RecordDepositCommand(
            UUID.fromString(testAccountId), new BigDecimal("1000000.00"), "JPY", "高額決済プロキシ入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(highValueDeposit)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount").value(1000000.00));

    // 残高確認
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(1000000.00));

    // 部分支払
    RecordPaymentCommand partialPayment =
        new RecordPaymentCommand(
            UUID.fromString(testAccountId), new BigDecimal("250000.00"), "JPY", "部分決済プロキシ支払");

    mockMvc
        .perform(
            post("/transactions/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialPayment)))
        .andExpect(status().isCreated());

    // 最終残高確認
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(750000.00));
  }

  @Test
  @DisplayName("決済プロキシ管理 - アカウント間連携")
  void paymentProxyManagement_CrossAccountIntegration() throws Exception {
    // 別のアカウント作成
    User anotherRequester =
        User.create(
            new Provider("e2e-provider"),
            "another-requester-" + System.currentTimeMillis(),
            "Another Requester",
            "another.requester@e2e.test");
    userRepository.save(anotherRequester);

    User anotherPayer =
        User.create(
            new Provider("e2e-provider"),
            "another-payer-" + System.currentTimeMillis(),
            "Another Payer",
            "another.payer@e2e.test");
    userRepository.save(anotherPayer);

    CreateAccountCommand anotherAccountCommand =
        new CreateAccountCommand(anotherRequester.getId(), anotherPayer.getId());

    String anotherAccountResponse =
        mockMvc
            .perform(
                post("/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(anotherAccountCommand)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String anotherAccountId = objectMapper.readTree(anotherAccountResponse).get("id").asText();

    // 両アカウントでの取引
    RecordDepositCommand deposit1 =
        new RecordDepositCommand(
            UUID.fromString(testAccountId), BigDecimal.valueOf(30000.00), "JPY", "アカウント1入金");

    RecordDepositCommand deposit2 =
        new RecordDepositCommand(
            UUID.fromString(anotherAccountId), BigDecimal.valueOf(20000.00), "JPY", "アカウント2入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deposit1)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deposit2)))
        .andExpect(status().isCreated());

    // 各アカウントの残高確認
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(30000.00));

    mockMvc
        .perform(get("/transactions/balance").param("accountId", anotherAccountId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(20000.00));
  }
}

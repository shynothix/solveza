package com.shinkaji.solveza.api.transaction.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinkaji.solveza.api.account.domain.model.Account;
import com.shinkaji.solveza.api.account.domain.repository.AccountRepository;
import com.shinkaji.solveza.api.annotation.ControllerIntegrationTest;
import com.shinkaji.solveza.api.shared.domain.Provider;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.transaction.application.command.RecordDepositCommand;
import com.shinkaji.solveza.api.transaction.application.command.RecordPaymentCommand;
import com.shinkaji.solveza.api.usermanagement.domain.model.User;
import com.shinkaji.solveza.api.usermanagement.domain.repository.UserRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ControllerIntegrationTest
@DisplayName("TransactionController Integration Tests")
class TransactionControllerIntegrationTest {

  private final WebApplicationContext webApplicationContext;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;

  TransactionControllerIntegrationTest(
      WebApplicationContext webApplicationContext,
      ObjectMapper objectMapper,
      UserRepository userRepository,
      AccountRepository accountRepository) {
    this.webApplicationContext = webApplicationContext;
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
  }

  private MockMvc mockMvc;
  private UUID testAccountId;

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

    User payer =
        User.create(
            new Provider("test-provider"),
            "test-payer-" + System.currentTimeMillis(),
            "Test Payer",
            "payer@test.com");
    userRepository.save(payer);

    // テスト用アカウントを作成
    Account account = Account.create(new UserId(requester.getId()), new UserId(payer.getId()));
    accountRepository.save(account);
    testAccountId = account.getId();
    System.out.println("Created test account with ID: " + testAccountId);
  }

  @Test
  @DisplayName("入金記録 - 正常ケース")
  void recordDeposit_Success() throws Exception {
    RecordDepositCommand request =
        new RecordDepositCommand(testAccountId, BigDecimal.valueOf(10000.00), "JPY", "給与振込");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
        .andExpect(jsonPath("$.amount").value(10000.00))
        .andExpect(jsonPath("$.currency").value("JPY"))
        .andExpect(jsonPath("$.description").value("給与振込"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  @DisplayName("支払記録 - 正常ケース")
  void recordPayment_Success() throws Exception {
    RecordPaymentCommand request =
        new RecordPaymentCommand(testAccountId, BigDecimal.valueOf(5000.00), "JPY", "食費");

    mockMvc
        .perform(
            post("/transactions/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionType").value("PAYMENT"))
        .andExpect(jsonPath("$.amount").value(5000.00))
        .andExpect(jsonPath("$.currency").value("JPY"))
        .andExpect(jsonPath("$.description").value("食費"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  @DisplayName("取引履歴取得")
  void getTransactionHistory_Success() throws Exception {
    mockMvc
        .perform(get("/transactions/history").param("accountId", testAccountId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("口座残高取得")
  void getAccountBalance_Success() throws Exception {
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(testAccountId.toString()))
        .andExpect(jsonPath("$.amount").exists())
        .andExpect(jsonPath("$.currency").exists());
  }

  @Test
  @DisplayName("入金記録 - バリデーションエラー（負の金額）")
  void recordDeposit_ValidationError_NegativeAmount() throws Exception {
    RecordDepositCommand request =
        new RecordDepositCommand(
            testAccountId,
            BigDecimal.valueOf(-1000.00), // 負の値
            "JPY",
            "無効な入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("支払記録 - バリデーションエラー（ゼロ金額）")
  void recordPayment_ValidationError_ZeroAmount() throws Exception {
    RecordPaymentCommand request =
        new RecordPaymentCommand(
            testAccountId,
            BigDecimal.ZERO, // ゼロ
            "JPY",
            "無効な支払");

    mockMvc
        .perform(
            post("/transactions/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("入金記録 - バリデーションエラー（空の説明）")
  void recordDeposit_ValidationError_EmptyDescription() throws Exception {
    RecordDepositCommand request =
        new RecordDepositCommand(
            testAccountId, BigDecimal.valueOf(1000.00), "JPY", "" // 空の説明
            );

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("存在しない口座の残高取得エラー")
  void getAccountBalance_NotFound() throws Exception {
    UUID nonExistentAccountId = UUID.randomUUID();

    mockMvc
        .perform(get("/transactions/balance").param("accountId", nonExistentAccountId.toString()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("無効なUUID形式エラー")
  void getTransactionHistory_InvalidUuidFormat() throws Exception {
    mockMvc
        .perform(get("/transactions/history").param("accountId", "invalid-uuid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("入金後の残高確認")
  void depositAndCheckBalance_Success() throws Exception {
    // 入金記録
    RecordDepositCommand depositRequest =
        new RecordDepositCommand(testAccountId, BigDecimal.valueOf(15000.00), "JPY", "テスト入金");

    mockMvc
        .perform(
            post("/transactions/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
        .andExpect(status().isCreated());

    // 残高確認
    mockMvc
        .perform(get("/transactions/balance").param("accountId", testAccountId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(15000.00));
  }
}

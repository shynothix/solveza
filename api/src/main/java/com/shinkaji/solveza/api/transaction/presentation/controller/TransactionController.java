package com.shinkaji.solveza.api.transaction.presentation.controller;

import com.shinkaji.solveza.api.transaction.application.command.RecordDepositCommand;
import com.shinkaji.solveza.api.transaction.application.command.RecordPaymentCommand;
import com.shinkaji.solveza.api.transaction.application.query.GetAccountBalanceQuery;
import com.shinkaji.solveza.api.transaction.application.query.GetTransactionHistoryQuery;
import com.shinkaji.solveza.api.transaction.application.usecase.GetAccountBalanceUseCase;
import com.shinkaji.solveza.api.transaction.application.usecase.GetTransactionHistoryUseCase;
import com.shinkaji.solveza.api.transaction.application.usecase.RecordDepositUseCase;
import com.shinkaji.solveza.api.transaction.application.usecase.RecordPaymentUseCase;
import com.shinkaji.solveza.api.transaction.presentation.dto.BalanceDto;
import com.shinkaji.solveza.api.transaction.presentation.dto.TransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction Management", description = "取引管理API")
public class TransactionController {

  private final RecordDepositUseCase recordDepositUseCase;
  private final RecordPaymentUseCase recordPaymentUseCase;
  private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;
  private final GetAccountBalanceUseCase getAccountBalanceUseCase;

  public TransactionController(
      RecordDepositUseCase recordDepositUseCase,
      RecordPaymentUseCase recordPaymentUseCase,
      GetTransactionHistoryUseCase getTransactionHistoryUseCase,
      GetAccountBalanceUseCase getAccountBalanceUseCase) {
    this.recordDepositUseCase = recordDepositUseCase;
    this.recordPaymentUseCase = recordPaymentUseCase;
    this.getTransactionHistoryUseCase = getTransactionHistoryUseCase;
    this.getAccountBalanceUseCase = getAccountBalanceUseCase;
  }

  @PostMapping("/deposits")
  @Operation(summary = "預かり取引記録", description = "新しい預かり取引を記録します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "預かり取引が正常に記録されました"),
        @ApiResponse(responseCode = "400", description = "リクエストが無効です"),
        @ApiResponse(responseCode = "404", description = "アカウントが見つかりません")
      })
  public ResponseEntity<TransactionDto> recordDeposit(
      @Valid @RequestBody RecordDepositCommand command) {
    TransactionDto transaction = recordDepositUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
  }

  @PostMapping("/payments")
  @Operation(summary = "支払い取引記録", description = "新しい支払い取引を記録します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "支払い取引が正常に記録されました"),
        @ApiResponse(responseCode = "400", description = "リクエストが無効です"),
        @ApiResponse(responseCode = "404", description = "アカウントが見つかりません")
      })
  public ResponseEntity<TransactionDto> recordPayment(
      @Valid @RequestBody RecordPaymentCommand command) {
    TransactionDto transaction = recordPaymentUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
  }

  @GetMapping("/history")
  @Operation(summary = "取引履歴取得", description = "指定されたアカウントの取引履歴を取得します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "取引履歴が正常に取得されました"),
        @ApiResponse(responseCode = "400", description = "リクエストが無効です"),
        @ApiResponse(responseCode = "404", description = "アカウントが見つかりません")
      })
  public ResponseEntity<List<TransactionDto>> getTransactionHistory(
      @Parameter(description = "アカウントID", required = true) @RequestParam UUID accountId) {
    GetTransactionHistoryQuery query = new GetTransactionHistoryQuery(accountId);
    List<TransactionDto> transactions = getTransactionHistoryUseCase.execute(query);
    return ResponseEntity.ok(transactions);
  }

  @GetMapping("/balance")
  @Operation(summary = "アカウント残高取得", description = "指定されたアカウントの残高を取得します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "残高が正常に取得されました"),
        @ApiResponse(responseCode = "400", description = "リクエストが無効です"),
        @ApiResponse(responseCode = "404", description = "アカウントが見つかりません")
      })
  public ResponseEntity<BalanceDto> getAccountBalance(
      @Parameter(description = "アカウントID", required = true) @RequestParam UUID accountId) {
    GetAccountBalanceQuery query = new GetAccountBalanceQuery(accountId);
    BalanceDto balance = getAccountBalanceUseCase.execute(query);
    return ResponseEntity.ok(balance);
  }
}

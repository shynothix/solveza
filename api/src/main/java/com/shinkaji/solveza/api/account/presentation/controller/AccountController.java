package com.shinkaji.solveza.api.account.presentation.controller;

import com.shinkaji.solveza.api.account.application.command.CreateAccountCommand;
import com.shinkaji.solveza.api.account.application.command.DeleteAccountCommand;
import com.shinkaji.solveza.api.account.application.query.GetAccountQuery;
import com.shinkaji.solveza.api.account.application.query.GetAccountsByUserQuery;
import com.shinkaji.solveza.api.account.application.usecase.CreateAccountUseCase;
import com.shinkaji.solveza.api.account.application.usecase.DeleteAccountUseCase;
import com.shinkaji.solveza.api.account.application.usecase.GetAccountUseCase;
import com.shinkaji.solveza.api.account.application.usecase.GetAccountsByUserUseCase;
import com.shinkaji.solveza.api.account.presentation.dto.AccountDto;
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
@RequestMapping("/accounts")
@Tag(name = "Account Management", description = "アカウント管理API")
public class AccountController {

  private final CreateAccountUseCase createAccountUseCase;
  private final DeleteAccountUseCase deleteAccountUseCase;
  private final GetAccountUseCase getAccountUseCase;
  private final GetAccountsByUserUseCase getAccountsByUserUseCase;

  public AccountController(
      CreateAccountUseCase createAccountUseCase,
      DeleteAccountUseCase deleteAccountUseCase,
      GetAccountUseCase getAccountUseCase,
      GetAccountsByUserUseCase getAccountsByUserUseCase) {
    this.createAccountUseCase = createAccountUseCase;
    this.deleteAccountUseCase = deleteAccountUseCase;
    this.getAccountUseCase = getAccountUseCase;
    this.getAccountsByUserUseCase = getAccountsByUserUseCase;
  }

  @PostMapping
  @Operation(summary = "アカウント作成", description = "新しいアカウントを作成します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "アカウントが正常に作成されました"),
        @ApiResponse(responseCode = "400", description = "リクエストが無効です"),
        @ApiResponse(responseCode = "409", description = "アカウントが既に存在します")
      })
  public ResponseEntity<AccountDto> createAccount(
      @Valid @RequestBody CreateAccountCommand command) {
    AccountDto account = createAccountUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(account);
  }

  @GetMapping("/{accountId}")
  @Operation(summary = "アカウント取得", description = "指定されたIDのアカウントを取得します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "アカウントが正常に取得されました"),
        @ApiResponse(responseCode = "404", description = "アカウントが見つかりません")
      })
  public ResponseEntity<AccountDto> getAccount(
      @Parameter(description = "アカウントID", required = true) @PathVariable UUID accountId) {
    GetAccountQuery query = new GetAccountQuery(accountId);
    AccountDto account = getAccountUseCase.execute(query);
    return ResponseEntity.ok(account);
  }

  @GetMapping
  @Operation(summary = "ユーザー関連アカウント一覧取得", description = "指定されたユーザーに関連するアカウントの一覧を取得します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "アカウント一覧が正常に取得されました"),
        @ApiResponse(responseCode = "400", description = "リクエストが無効です")
      })
  public ResponseEntity<List<AccountDto>> getAccountsByUser(
      @Parameter(description = "ユーザーID", required = true) @RequestParam UUID userId) {
    GetAccountsByUserQuery query = new GetAccountsByUserQuery(userId);
    List<AccountDto> accounts = getAccountsByUserUseCase.execute(query);
    return ResponseEntity.ok(accounts);
  }

  @DeleteMapping("/{accountId}")
  @Operation(summary = "アカウント削除", description = "指定されたIDのアカウントを削除します")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "アカウントが正常に削除されました"),
        @ApiResponse(responseCode = "404", description = "アカウントが見つかりません")
      })
  public ResponseEntity<Void> deleteAccount(
      @Parameter(description = "アカウントID", required = true) @PathVariable UUID accountId) {
    DeleteAccountCommand command = new DeleteAccountCommand(accountId);
    deleteAccountUseCase.execute(command);
    return ResponseEntity.noContent().build();
  }
}

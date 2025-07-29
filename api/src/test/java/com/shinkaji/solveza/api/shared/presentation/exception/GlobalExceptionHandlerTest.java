package com.shinkaji.solveza.api.shared.presentation.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.shinkaji.solveza.api.shared.domain.AccountId;
import com.shinkaji.solveza.api.shared.domain.UserId;
import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import com.shinkaji.solveza.api.shared.domain.exception.DuplicateAccountException;
import com.shinkaji.solveza.api.shared.domain.exception.InsufficientPermissionException;
import com.shinkaji.solveza.api.shared.domain.exception.InvalidTransactionException;
import com.shinkaji.solveza.api.shared.domain.exception.UserNotFoundException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@DisplayName("GlobalExceptionHandlerのテスト")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  @DisplayName("AccountNotFoundExceptionを適切に処理する")
  void handleAccountNotFound_shouldReturnNotFound() {
    // Given
    AccountId accountId = new AccountId(UUID.randomUUID());
    AccountNotFoundException exception = new AccountNotFoundException(accountId);

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleAccountNotFound(exception);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("ACCOUNT_NOT_FOUND", response.getBody().code());
    assertTrue(response.getBody().message().contains("アカウントが見つかりません"));
  }

  @Test
  @DisplayName("InsufficientPermissionExceptionを適切に処理する")
  void handleInsufficientPermission_shouldReturnForbidden() {
    // Given
    InsufficientPermissionException exception = new InsufficientPermissionException("テスト操作");

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleInsufficientPermission(exception);

    // Then
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INSUFFICIENT_PERMISSION", response.getBody().code());
    assertTrue(response.getBody().message().contains("操作に必要な権限がありません"));
  }

  @Test
  @DisplayName("InvalidTransactionExceptionを適切に処理する")
  void handleInvalidTransaction_shouldReturnBadRequest() {
    // Given
    InvalidTransactionException exception = new InvalidTransactionException("無効な取引");

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleInvalidTransaction(exception);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INVALID_TRANSACTION", response.getBody().code());
    assertTrue(response.getBody().message().contains("無効な取引です"));
  }

  @Test
  @DisplayName("UserNotFoundExceptionを適切に処理する")
  void handleUserNotFound_shouldReturnNotFound() {
    // Given
    UserId userId = new UserId(UUID.randomUUID());
    UserNotFoundException exception = new UserNotFoundException(userId);

    // When
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserNotFound(exception);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("USER_NOT_FOUND", response.getBody().code());
    assertTrue(response.getBody().message().contains("ユーザーが見つかりません"));
  }

  @Test
  @DisplayName("DuplicateAccountExceptionを適切に処理する")
  void handleDuplicateAccount_shouldReturnConflict() {
    // Given
    UserId requesterId = new UserId(UUID.randomUUID());
    UserId payerId = new UserId(UUID.randomUUID());
    DuplicateAccountException exception = new DuplicateAccountException(requesterId, payerId);

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleDuplicateAccount(exception);

    // Then
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("DUPLICATE_ACCOUNT", response.getBody().code());
    assertTrue(response.getBody().message().contains("既に存在します"));
  }

  @Test
  @DisplayName("MethodArgumentNotValidExceptionを適切に処理する")
  void handleValidationErrors_shouldReturnBadRequest() {
    // Given
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("testObject", "testField", "テストエラーメッセージ");

    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

    // When
    ResponseEntity<ValidationErrorResponse> response =
        globalExceptionHandler.handleValidationErrors(exception);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("VALIDATION_ERROR", response.getBody().code());
    assertEquals("入力値が無効です", response.getBody().message());
    assertTrue(response.getBody().fieldErrors().containsKey("testField"));
    assertEquals("テストエラーメッセージ", response.getBody().fieldErrors().get("testField"));
  }

  @Test
  @DisplayName("IllegalArgumentExceptionを適切に処理する")
  void handleIllegalArgument_shouldReturnBadRequest() {
    // Given
    IllegalArgumentException exception = new IllegalArgumentException("不正な引数です");

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleIllegalArgument(exception);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("ILLEGAL_ARGUMENT", response.getBody().code());
    assertEquals("不正な引数です", response.getBody().message());
  }

  @Test
  @DisplayName("RuntimeExceptionを適切に処理する")
  void handleRuntimeException_shouldReturnInternalServerError() {
    // Given
    RuntimeException exception = new RuntimeException("ランタイムエラー");

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleRuntimeException(exception);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INTERNAL_SERVER_ERROR", response.getBody().code());
    assertEquals("内部サーバーエラーが発生しました", response.getBody().message());
  }

  @Test
  @DisplayName("一般的なExceptionを適切に処理する")
  void handleGenericException_shouldReturnInternalServerError() {
    // Given
    Exception exception = new Exception("予期しないエラー");

    // When
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleGenericException(exception);

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INTERNAL_SERVER_ERROR", response.getBody().code());
    assertEquals("予期しないエラーが発生しました", response.getBody().message());
  }
}

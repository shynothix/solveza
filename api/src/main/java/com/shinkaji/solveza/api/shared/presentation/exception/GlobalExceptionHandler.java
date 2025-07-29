package com.shinkaji.solveza.api.shared.presentation.exception;

import com.shinkaji.solveza.api.shared.domain.exception.AccountNotFoundException;
import com.shinkaji.solveza.api.shared.domain.exception.DuplicateAccountException;
import com.shinkaji.solveza.api.shared.domain.exception.InsufficientPermissionException;
import com.shinkaji.solveza.api.shared.domain.exception.InvalidTransactionException;
import com.shinkaji.solveza.api.shared.domain.exception.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("ACCOUNT_NOT_FOUND", e.getMessage()));
  }

  @ExceptionHandler(InsufficientPermissionException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientPermission(
      InsufficientPermissionException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponse("INSUFFICIENT_PERMISSION", e.getMessage()));
  }

  @ExceptionHandler(InvalidTransactionException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTransaction(InvalidTransactionException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("INVALID_TRANSACTION", e.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
  }

  @ExceptionHandler(DuplicateAccountException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateAccount(DuplicateAccountException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse("DUPLICATE_ACCOUNT", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationErrorResponse("VALIDATION_ERROR", "入力値が無効です", errors));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
    String message = String.format("パラメータ '%s' の値 '%s' は無効です", e.getName(), e.getValue());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("INVALID_PARAMETER", message));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
    log.error("実行時例外が発生しました", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "内部サーバーエラーが発生しました"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    log.error("予期しない例外が発生しました", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "予期しないエラーが発生しました"));
  }
}

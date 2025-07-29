package com.shinkaji.solveza.api.shared.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Money値オブジェクトのテスト")
class MoneyTest {

  @Test
  @DisplayName("有効な金額と通貨でMoneyを作成できる")
  void createMoney_shouldSucceed_whenValidAmountAndCurrency() {
    // Given
    BigDecimal amount = BigDecimal.valueOf(1000);
    Currency currency = Currency.getInstance("JPY");

    // When
    Money money = new Money(amount, currency);

    // Then
    assertEquals(amount, money.amount());
    assertEquals(currency, money.currency());
  }

  @Test
  @DisplayName("負の金額でMoneyを作成するとエラーが発生する")
  void createMoney_shouldThrowException_whenNegativeAmount() {
    // Given
    BigDecimal negativeAmount = BigDecimal.valueOf(-100);
    Currency currency = Currency.getInstance("JPY");

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> new Money(negativeAmount, currency));
  }

  @Test
  @DisplayName("null金額でMoneyを作成するとエラーが発生する")
  void createMoney_shouldThrowException_whenNullAmount() {
    // Given
    Currency currency = Currency.getInstance("JPY");

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> new Money(null, currency));
  }

  @Test
  @DisplayName("null通貨でMoneyを作成するとエラーが発生する")
  void createMoney_shouldThrowException_whenNullCurrency() {
    // Given
    BigDecimal amount = BigDecimal.valueOf(1000);

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> new Money(amount, null));
  }

  @Test
  @DisplayName("JPY通貨のMoneyを便利メソッドで作成できる")
  void createYenMoney_shouldSucceed_whenValidAmount() {
    // Given
    BigDecimal amount = BigDecimal.valueOf(1000);

    // When
    Money money = Money.yen(amount);

    // Then
    assertEquals(amount, money.amount());
    assertEquals(Currency.getInstance("JPY"), money.currency());
  }

  @Test
  @DisplayName("long値からJPY通貨のMoneyを作成できる")
  void createYenMoneyFromLong_shouldSucceed_whenValidAmount() {
    // Given
    long amount = 1000L;

    // When
    Money money = Money.yen(amount);

    // Then
    assertEquals(BigDecimal.valueOf(amount), money.amount());
    assertEquals(Currency.getInstance("JPY"), money.currency());
  }

  @Test
  @DisplayName("同じ通貨のMoneyを加算できる")
  void addMoney_shouldSucceed_whenSameCurrency() {
    // Given
    Money money1 = Money.yen(1000);
    Money money2 = Money.yen(500);

    // When
    Money result = money1.add(money2);

    // Then
    assertEquals(Money.yen(1500), result);
  }

  @Test
  @DisplayName("異なる通貨のMoneyを加算するとエラーが発生する")
  void addMoney_shouldThrowException_whenDifferentCurrency() {
    // Given
    Money yenMoney = Money.yen(1000);
    Money usdMoney = new Money(BigDecimal.valueOf(10), Currency.getInstance("USD"));

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> yenMoney.add(usdMoney));
  }

  @Test
  @DisplayName("同じ通貨のMoneyを減算できる")
  void subtractMoney_shouldSucceed_whenSameCurrency() {
    // Given
    Money money1 = Money.yen(1000);
    Money money2 = Money.yen(300);

    // When
    Money result = money1.subtract(money2);

    // Then
    assertEquals(Money.yen(700), result);
  }

  @Test
  @DisplayName("金額が0かどうかを正しく判定できる")
  void isZero_shouldReturnCorrectResult() {
    // Given
    Money zeroMoney = Money.yen(0);
    Money nonZeroMoney = Money.yen(100);

    // When & Then
    assertTrue(zeroMoney.isZero());
    assertFalse(nonZeroMoney.isZero());
  }

  @Test
  @DisplayName("金額が正の値かどうかを正しく判定できる")
  void isPositive_shouldReturnCorrectResult() {
    // Given
    Money zeroMoney = Money.yen(0);
    Money positiveMoney = Money.yen(100);

    // When & Then
    assertFalse(zeroMoney.isPositive());
    assertTrue(positiveMoney.isPositive());
  }
}

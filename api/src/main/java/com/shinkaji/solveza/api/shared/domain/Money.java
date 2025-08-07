package com.shinkaji.solveza.api.shared.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

  public Money {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("金額は0以上である必要があります");
    }
    if (currency == null) {
      throw new IllegalArgumentException("通貨は必須です");
    }
  }

  public static Money yen(BigDecimal amount) {
    return new Money(amount, Currency.getInstance("JPY"));
  }

  public static Money yen(long amount) {
    return new Money(BigDecimal.valueOf(amount), Currency.getInstance("JPY"));
  }

  public Money add(Money other) {
    validateSameCurrency(other);
    return new Money(amount.add(other.amount), currency);
  }

  public Money subtract(Money other) {
    validateSameCurrency(other);
    return new Money(amount.subtract(other.amount), currency);
  }

  public boolean isZero() {
    return amount.compareTo(BigDecimal.ZERO) == 0;
  }

  public boolean isPositive() {
    return amount.compareTo(BigDecimal.ZERO) > 0;
  }

  private void validateSameCurrency(Money other) {
    if (!currency.equals(other.currency)) {
      throw new IllegalArgumentException("異なる通貨での計算はできません");
    }
  }
}

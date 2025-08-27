package io.github.flowrapp.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import lombok.NonNull;

public record Seconds(
    @NonNull BigInteger seconds) {

  public static final Seconds ZERO = zero();

  public static final int SECONDS_IN_HOUR = 3600;

  public static final BigDecimal SECONDS_IN_HOUR_BG = BigDecimal.valueOf(SECONDS_IN_HOUR);

  public static Seconds zero() {
    return of(BigInteger.ZERO);
  }

  public static Seconds of(int seconds) {
    return of(BigInteger.valueOf(seconds));
  }

  public static Seconds of(@NonNull BigInteger seconds) {
    return new Seconds(seconds);
  }

  public Seconds add(@NonNull Seconds other) {
    return new Seconds(this.seconds.add(other.seconds));
  }

  public Seconds add(@NonNull BigInteger seconds) {
    return new Seconds(this.seconds.add(seconds));
  }

  public Seconds minus(@NonNull Seconds other) {
    return new Seconds(this.seconds.subtract(other.seconds));
  }

  public Seconds minus(@NonNull BigInteger seconds) {
    return new Seconds(this.seconds.subtract(seconds));
  }

  public BigDecimal asHours() {
    return new BigDecimal(seconds)
        .divide(SECONDS_IN_HOUR_BG, 2, RoundingMode.FLOOR);
  }

}

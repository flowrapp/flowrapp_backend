package io.github.flowrapp.model;

import java.math.BigInteger;

import lombok.NonNull;
import lombok.val;

public record Seconds(
    @NonNull BigInteger seconds) {

  public static final Seconds ZERO = of(BigInteger.ZERO);

  public static final int SECONDS_IN_HOUR = 3600;

  public static final int SECONDS_IN_MINUTE = 60;

  public static final BigInteger SECONDS_IN_HOUR_BI = BigInteger.valueOf(SECONDS_IN_HOUR);

  public static final BigInteger SECONDS_IN_MINUTE_BI = BigInteger.valueOf(SECONDS_IN_MINUTE);

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

  /**
   * Formats the duration in "Xh Ym" format, where X is hours and Y is minutes.
   */
  public String formatted() {
    val hours = seconds.divide(SECONDS_IN_HOUR_BI);
    val minutes = seconds.remainder(SECONDS_IN_HOUR_BI).divide(SECONDS_IN_MINUTE_BI);

    return hours + "h " + minutes + "m";
  }

}

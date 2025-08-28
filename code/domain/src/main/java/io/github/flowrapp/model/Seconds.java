package io.github.flowrapp.model;

import static java.text.MessageFormat.format;

import java.math.BigInteger;

import lombok.NonNull;

public record Seconds(
    @NonNull BigInteger seconds) {

  public static final Seconds ZERO = of(BigInteger.ZERO);

  public static final int SECONDS_IN_HOUR = 3600;

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

  // Should format in the format: HH MM
  public String formatted() {
    long totalSeconds = seconds.longValue();
    long hours = totalSeconds / SECONDS_IN_HOUR;
    long minutes = (totalSeconds % SECONDS_IN_HOUR) / 60;
    return format("{0}h {1}m", hours, minutes);
  }

}

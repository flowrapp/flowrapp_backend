package io.github.flowrapp.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {

  public static final BigDecimal SECONDS_IN_HOUR = BigDecimal.valueOf(3600);

  public static BigDecimal secondsToHours(BigInteger seconds) {
    Objects.requireNonNull(seconds, "seconds");
    return new BigDecimal(seconds)
            .divide(SECONDS_IN_HOUR, 2, RoundingMode.FLOOR);
  }

}

package io.github.flowrapp.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {

  public static final int SECONDS_IN_HOUR = 3600;

  public static Function<BigInteger, BigDecimal> secondsToHours() {
    return seconds -> new BigDecimal(seconds)
        .divide(BigDecimal.valueOf(SECONDS_IN_HOUR), 2, RoundingMode.DOWN);
  }

}

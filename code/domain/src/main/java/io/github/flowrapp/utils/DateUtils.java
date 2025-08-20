package io.github.flowrapp.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

  public static final UnaryOperator<OffsetDateTime> atStartOfDay = dateTime -> dateTime.toLocalDate()
      .atStartOfDay()
      .atOffset(dateTime.getOffset());

  public static final UnaryOperator<OffsetDateTime> atEndOfDay = dateTime -> dateTime.toLocalDate()
      .plusDays(1)
      .atStartOfDay()
      .atOffset(dateTime.getOffset())
      .minusNanos(1);

  public static UnaryOperator<OffsetDateTime> toZone(ZoneId zoneId) {
    return offsetDateTime -> toZone.apply(offsetDateTime, zoneId);
  }

  public static final BiFunction<OffsetDateTime, ZoneId, OffsetDateTime> toZone =
      (dateTime, zoneOffset) -> dateTime.atZoneSameInstant(zoneOffset).toOffsetDateTime();

}

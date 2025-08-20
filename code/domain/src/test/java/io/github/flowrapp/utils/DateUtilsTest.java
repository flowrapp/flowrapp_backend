package io.github.flowrapp.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DateUtils Tests")
class DateUtilsTest {

  @Nested
  @DisplayName("atStartOfDay")
  class AtStartOfDayTests {

    @Test
    @DisplayName("Should set time to start of day while preserving offset")
    void shouldSetTimeToStartOfDayWhilePreservingOffset() {
      // Given
      OffsetDateTime dateTime = OffsetDateTime.of(2024, 1, 15, 14, 30, 45, 123456789, ZoneOffset.ofHours(2));

      // When
      OffsetDateTime result = DateUtils.atStartOfDay.apply(dateTime);

      // Then
      assertThat(result.toLocalDate()).isEqualTo(dateTime.toLocalDate());
      assertThat(result.toLocalTime().getHour()).isZero();
      assertThat(result.toLocalTime().getMinute()).isZero();
      assertThat(result.toLocalTime().getSecond()).isZero();
      assertThat(result.toLocalTime().getNano()).isZero();
      assertThat(result.getOffset()).isEqualTo(dateTime.getOffset());
    }

    @Test
    @DisplayName("Should work with different time zones")
    void shouldWorkWithDifferentTimeZones() {
      // Given
      OffsetDateTime utcDateTime = OffsetDateTime.of(2024, 6, 20, 18, 45, 30, 0, ZoneOffset.UTC);
      OffsetDateTime negativeOffsetDateTime = OffsetDateTime.of(2024, 6, 20, 18, 45, 30, 0, ZoneOffset.ofHours(-5));

      // When
      OffsetDateTime utcResult = DateUtils.atStartOfDay.apply(utcDateTime);
      OffsetDateTime negativeOffsetResult = DateUtils.atStartOfDay.apply(negativeOffsetDateTime);

      // Then
      assertThat(utcResult.toLocalDate()).isEqualTo(LocalDate.of(2024, 6, 20));
      assertThat(utcResult.toLocalTime().getHour()).isZero();
      assertThat(utcResult.getOffset()).isEqualTo(ZoneOffset.UTC);

      assertThat(negativeOffsetResult.toLocalDate()).isEqualTo(LocalDate.of(2024, 6, 20));
      assertThat(negativeOffsetResult.toLocalTime().getHour()).isZero();
      assertThat(negativeOffsetResult.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
    }

    @Test
    @DisplayName("Should handle edge case with already at start of day")
    void shouldHandleEdgeCaseWithAlreadyAtStartOfDay() {
      // Given
      OffsetDateTime startOfDay = OffsetDateTime.of(2024, 12, 25, 0, 0, 0, 0, ZoneOffset.ofHours(1));

      // When
      OffsetDateTime result = DateUtils.atStartOfDay.apply(startOfDay);

      // Then
      assertThat(result).isEqualTo(startOfDay);
    }

    @Test
    @DisplayName("Should handle leap year date")
    void shouldHandleLeapYearDate() {
      // Given
      OffsetDateTime leapYearDate = OffsetDateTime.of(2024, 2, 29, 23, 59, 59, 999999999, ZoneOffset.ofHours(3));

      // When
      OffsetDateTime result = DateUtils.atStartOfDay.apply(leapYearDate);

      // Then
      assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2024, 2, 29));
      assertThat(result.toLocalTime().getHour()).isZero();
      assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(3));
    }
  }

  @Nested
  @DisplayName("atEndOfDay")
  class AtEndOfDayTests {

    @Test
    @DisplayName("Should set time to end of day while preserving offset")
    void shouldSetTimeToEndOfDayWhilePreservingOffset() {
      // Given
      OffsetDateTime dateTime = OffsetDateTime.of(2024, 1, 15, 14, 30, 45, 123456789, ZoneOffset.ofHours(2));

      // When
      OffsetDateTime result = DateUtils.atEndOfDay.apply(dateTime);

      // Then
      assertThat(result.toLocalDate()).isEqualTo(dateTime.toLocalDate());
      assertThat(result.toLocalTime().getHour()).isEqualTo(23);
      assertThat(result.toLocalTime().getMinute()).isEqualTo(59);
      assertThat(result.toLocalTime().getSecond()).isEqualTo(59);
      assertThat(result.toLocalTime().getNano()).isEqualTo(999999999);
      assertThat(result.getOffset()).isEqualTo(dateTime.getOffset());
    }

    @Test
    @DisplayName("Should work with different time zones")
    void shouldWorkWithDifferentTimeZones() {
      // Given
      OffsetDateTime utcDateTime = OffsetDateTime.of(2024, 6, 20, 8, 15, 30, 0, ZoneOffset.UTC);
      OffsetDateTime positiveOffsetDateTime = OffsetDateTime.of(2024, 6, 20, 8, 15, 30, 0, ZoneOffset.ofHours(9));

      // When
      OffsetDateTime utcResult = DateUtils.atEndOfDay.apply(utcDateTime);
      OffsetDateTime positiveOffsetResult = DateUtils.atEndOfDay.apply(positiveOffsetDateTime);

      // Then
      assertThat(utcResult.toLocalDate()).isEqualTo(LocalDate.of(2024, 6, 20));
      assertThat(utcResult.toLocalTime().getHour()).isEqualTo(23);
      assertThat(utcResult.toLocalTime().getMinute()).isEqualTo(59);
      assertThat(utcResult.toLocalTime().getSecond()).isEqualTo(59);
      assertThat(utcResult.toLocalTime().getNano()).isEqualTo(999999999);
      assertThat(utcResult.getOffset()).isEqualTo(ZoneOffset.UTC);

      assertThat(positiveOffsetResult.toLocalDate()).isEqualTo(LocalDate.of(2024, 6, 20));
      assertThat(positiveOffsetResult.toLocalTime().getHour()).isEqualTo(23);
      assertThat(positiveOffsetResult.getOffset()).isEqualTo(ZoneOffset.ofHours(9));
    }

    @Test
    @DisplayName("Should handle edge case with already at end of day")
    void shouldHandleEdgeCaseWithAlreadyAtEndOfDay() {
      // Given
      OffsetDateTime endOfDay = OffsetDateTime.of(2024, 12, 25, 23, 59, 59, 999999999, ZoneOffset.ofHours(-3));

      // When
      OffsetDateTime result = DateUtils.atEndOfDay.apply(endOfDay);

      // Then
      assertThat(result).isEqualTo(endOfDay);
    }

    @Test
    @DisplayName("Should handle month boundary correctly")
    void shouldHandleMonthBoundaryCorrectly() {
      // Given - Last day of January
      OffsetDateTime lastDayOfMonth = OffsetDateTime.of(2024, 1, 31, 12, 0, 0, 0, ZoneOffset.ofHours(1));

      // When
      OffsetDateTime result = DateUtils.atEndOfDay.apply(lastDayOfMonth);

      // Then
      assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2024, 1, 31));
      assertThat(result.toLocalTime().getHour()).isEqualTo(23);
      assertThat(result.toLocalTime().getMinute()).isEqualTo(59);
      assertThat(result.toLocalTime().getSecond()).isEqualTo(59);
      assertThat(result.toLocalTime().getNano()).isEqualTo(999999999);
    }

    @Test
    @DisplayName("Should handle leap year February 29th")
    void shouldHandleLeapYearFebruary29th() {
      // Given
      OffsetDateTime leapDay = OffsetDateTime.of(2024, 2, 29, 10, 30, 0, 0, ZoneOffset.ofHours(2));

      // When
      OffsetDateTime result = DateUtils.atEndOfDay.apply(leapDay);

      // Then
      assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2024, 2, 29));
      assertThat(result.toLocalTime().getHour()).isEqualTo(23);
      assertThat(result.toLocalTime().getMinute()).isEqualTo(59);
      assertThat(result.toLocalTime().getSecond()).isEqualTo(59);
      assertThat(result.toLocalTime().getNano()).isEqualTo(999999999);
    }
  }

  @Nested
  @DisplayName("toZoneFun")
  class ToZoneFunTests {

    @Test
    @DisplayName("Should create function that converts to specified zone")
    void shouldCreateFunctionThatConvertsToSpecifiedZone() {
      // Given
      OffsetDateTime utcDateTime = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
      ZoneId tokyoZone = ZoneId.of("Asia/Tokyo");

      // When
      var toTokyoFunction = DateUtils.toZoneFun(tokyoZone);
      OffsetDateTime result = toTokyoFunction.apply(utcDateTime);

      // Then
      assertThat(result.toInstant()).isEqualTo(utcDateTime.toInstant()); // Same instant
      assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(9)); // Tokyo is UTC+9 in summer
    }

    @Test
    @DisplayName("Should handle different source and target zones")
    void shouldHandleDifferentSourceAndTargetZones() {
      // Given
      OffsetDateTime newYorkDateTime = OffsetDateTime.of(2024, 12, 15, 14, 30, 0, 0, ZoneOffset.ofHours(-5));
      ZoneId londonZone = ZoneId.of("Europe/London");

      // When
      var toLondonFunction = DateUtils.toZoneFun(londonZone);
      OffsetDateTime result = toLondonFunction.apply(newYorkDateTime);

      // Then
      assertThat(result.toInstant()).isEqualTo(newYorkDateTime.toInstant()); // Same instant
      assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(0)); // London is UTC in winter
      assertThat(result.toLocalTime().getHour()).isEqualTo(19); // 14:30 -5 -> 19:30 +0
      assertThat(result.toLocalTime().getMinute()).isEqualTo(30);
    }

    @Test
    @DisplayName("Should handle same zone conversion")
    void shouldHandleSameZoneConversion() {
      // Given
      OffsetDateTime dateTime = OffsetDateTime.of(2024, 3, 10, 10, 15, 30, 0, ZoneOffset.ofHours(2));
      ZoneId sameZone = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(2));

      // When
      var toSameZoneFunction = DateUtils.toZoneFun(sameZone);
      OffsetDateTime result = toSameZoneFunction.apply(dateTime);

      // Then
      assertThat(result.toInstant()).isEqualTo(dateTime.toInstant());
      assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(2));
      assertThat(result.toLocalDateTime()).isEqualTo(dateTime.toLocalDateTime());
    }

    @Test
    @DisplayName("Should handle zone with daylight saving time")
    void shouldHandleZoneWithDaylightSavingTime() {
      // Given - Summer time in Europe/Berlin (UTC+2)
      OffsetDateTime utcSummer = OffsetDateTime.of(2024, 7, 15, 10, 0, 0, 0, ZoneOffset.UTC);
      // Given - Winter time in Europe/Berlin (UTC+1)
      OffsetDateTime utcWinter = OffsetDateTime.of(2024, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);
      ZoneId berlinZone = ZoneId.of("Europe/Berlin");

      // When
      var toBerlinFunction = DateUtils.toZoneFun(berlinZone);
      OffsetDateTime summerResult = toBerlinFunction.apply(utcSummer);
      OffsetDateTime winterResult = toBerlinFunction.apply(utcWinter);

      // Then
      assertThat(summerResult.getOffset()).isEqualTo(ZoneOffset.ofHours(2)); // CEST (UTC+2)
      assertThat(winterResult.getOffset()).isEqualTo(ZoneOffset.ofHours(1)); // CET (UTC+1)
      assertThat(summerResult.toLocalTime().getHour()).isEqualTo(12); // 10 UTC + 2 hours
      assertThat(winterResult.toLocalTime().getHour()).isEqualTo(11); // 10 UTC + 1 hour
    }
  }

  @Nested
  @DisplayName("toZone")
  class ToZoneTests {

    @Test
    @DisplayName("Should convert datetime to specified zone")
    void shouldConvertDatetimeToSpecifiedZone() {
      // Given
      OffsetDateTime sourceDateTime = OffsetDateTime.of(2024, 6, 15, 15, 45, 30, 0, ZoneOffset.ofHours(-7));
      ZoneId targetZone = ZoneId.of("UTC");

      // When
      OffsetDateTime result = DateUtils.toZone.apply(sourceDateTime, targetZone);

      // Then
      assertThat(result.toInstant()).isEqualTo(sourceDateTime.toInstant()); // Same instant
      assertThat(result.getOffset()).isEqualTo(ZoneOffset.UTC);
      assertThat(result.toLocalTime().getHour()).isEqualTo(22); // 15:45 -7 -> 22:45 UTC
      assertThat(result.toLocalTime().getMinute()).isEqualTo(45);
      assertThat(result.toLocalTime().getSecond()).isEqualTo(30);
    }

    @Test
    @DisplayName("Should handle cross-date boundary conversion")
    void shouldHandleCrossDateBoundaryConversion() {
      // Given - Late evening in Los Angeles
      OffsetDateTime laDateTime = OffsetDateTime.of(2024, 12, 31, 23, 30, 0, 0, ZoneOffset.ofHours(-8));
      ZoneId sydneyZone = ZoneId.of("Australia/Sydney");

      // When
      OffsetDateTime result = DateUtils.toZone.apply(laDateTime, sydneyZone);

      // Then
      assertThat(result.toInstant()).isEqualTo(laDateTime.toInstant());
      assertThat(result.toLocalDate()).isEqualTo(LocalDate.of(2025, 1, 1)); // Next day in Sydney
      assertThat(result.toLocalTime().getHour()).isEqualTo(18); // Approximate Sydney time (UTC+11 in summer)
    }

    @Test
    @DisplayName("Should preserve nanosecond precision")
    void shouldPreserveNanosecondPrecision() {
      // Given
      OffsetDateTime preciseDateTime = OffsetDateTime.of(2024, 5, 20, 12, 30, 45, 123456789, ZoneOffset.ofHours(3));
      ZoneId utcZone = ZoneId.of("UTC");

      // When
      OffsetDateTime result = DateUtils.toZone.apply(preciseDateTime, utcZone);

      // Then
      assertThat(result.toInstant()).isEqualTo(preciseDateTime.toInstant());
      assertThat(result.toLocalTime().getNano()).isEqualTo(123456789);
    }

    @Test
    @DisplayName("Should handle null zone gracefully")
    void shouldHandleNullZoneGracefully() {
      // Given
      OffsetDateTime dateTime = OffsetDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

      // When & Then
      assertThatThrownBy(() -> DateUtils.toZone.apply(dateTime, null))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should handle null datetime gracefully")
    void shouldHandleNullDatetimeGracefully() {
      // Given
      ZoneId zone = ZoneId.of("UTC");

      // When & Then
      assertThatThrownBy(() -> DateUtils.toZone.apply(null, zone))
          .isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("Integration Tests")
  class IntegrationTests {

    @Test
    @DisplayName("Should work correctly when chaining multiple operations")
    void shouldWorkCorrectlyWhenChainingMultipleOperations() {
      // Given
      OffsetDateTime sourceDateTime = OffsetDateTime.of(2024, 8, 15, 14, 30, 45, 123456789, ZoneOffset.ofHours(-5));
      ZoneId targetZone = ZoneId.of("Europe/Paris");

      // When - Chain multiple operations
      OffsetDateTime result = DateUtils.toZoneFun(targetZone)
          .andThen(DateUtils.atStartOfDay)
          .apply(sourceDateTime);

      // Then
      assertThat(result.toLocalTime().getHour()).isZero();
      assertThat(result.toLocalTime().getMinute()).isZero();
      assertThat(result.toLocalTime().getSecond()).isZero();
      assertThat(result.toLocalTime().getNano()).isZero();
      assertThat(result.getOffset()).isEqualTo(ZoneOffset.ofHours(2)); // Paris in summer (CEST)
    }

    @Test
    @DisplayName("Should handle start and end of day for same date")
    void shouldHandleStartAndEndOfDayForSameDate() {
      // Given
      OffsetDateTime midDay = OffsetDateTime.of(2024, 3, 15, 12, 30, 0, 0, ZoneOffset.ofHours(1));

      // When
      OffsetDateTime startOfDay = DateUtils.atStartOfDay.apply(midDay);
      OffsetDateTime endOfDay = DateUtils.atEndOfDay.apply(midDay);

      // Then
      assertThat(startOfDay.toLocalDate()).isEqualTo(endOfDay.toLocalDate());
      assertThat(startOfDay.isBefore(endOfDay)).isTrue();
      assertThat(startOfDay.toLocalTime().getHour()).isZero();
      assertThat(endOfDay.toLocalTime().getHour()).isEqualTo(23);
      assertThat(startOfDay.getOffset()).isEqualTo(endOfDay.getOffset());
    }

    @Test
    @DisplayName("Should maintain consistency across different time zones")
    void shouldMaintainConsistencyAcrossDifferentTimeZones() {
      // Given
      OffsetDateTime baseDateTime = OffsetDateTime.of(2024, 6, 21, 10, 0, 0, 0, ZoneOffset.UTC);
      ZoneId[] zones = {
          ZoneId.of("America/New_York"),
          ZoneId.of("Europe/London"),
          ZoneId.of("Asia/Tokyo"),
          ZoneId.of("Australia/Sydney")
      };

      // When & Then
      for (ZoneId zone : zones) {
        OffsetDateTime converted = DateUtils.toZone.apply(baseDateTime, zone);
        OffsetDateTime startOfDay = DateUtils.atStartOfDay.apply(converted);
        OffsetDateTime endOfDay = DateUtils.atEndOfDay.apply(converted);

        assertThat(converted.toInstant()).isEqualTo(baseDateTime.toInstant());
        assertThat(startOfDay.toLocalTime().getHour()).isZero();
        assertThat(endOfDay.toLocalTime().getHour()).isEqualTo(23);
        assertThat(startOfDay.toLocalDate()).isEqualTo(endOfDay.toLocalDate());
      }
    }
  }
}

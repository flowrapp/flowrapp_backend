package io.github.flowrapp.model;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@With
public record Worklog(
    Integer id,
    User user,
    Business business,
    OffsetDateTime clockIn,
    OffsetDateTime clockOut,
    Instant createdAt) {

  public boolean isOwner(@NonNull User user) {
    return this.user.id().equals(user.id());
  }

  public boolean isValid() {
    return clockIn != null
        && clockOut != null
        && clockIn.isBefore(clockOut)
        && clockOut.isBefore(OffsetDateTime.now(business.zone()))
        && Duration.between(clockIn, clockOut).compareTo(Duration.ofDays(1)) <= 0;
  }

  public boolean isOpen() {
    return clockIn != null && clockOut == null;
  }

  public boolean isClosed() {
    return !isOpen();
  }

  public @Nullable LocalDate getDay() {
    return clockIn != null ? clockIn.atZoneSameInstant(business.zone()).toLocalDate() : null;
  }

  public BigInteger getSeconds() {
    if (clockIn == null || clockOut == null) {
      return BigInteger.ZERO;
    }

    return BigInteger.valueOf(
        Duration.between(clockIn, clockOut).toSeconds());
  }

  public Worklog toBusinessZone() {
    return toBuilder()
        .clockIn(clockIn.atZoneSameInstant(business.zone()).toOffsetDateTime())
        .clockOut(clockOut != null ? clockOut.atZoneSameInstant(business.zone()).toOffsetDateTime() : null)
        .build();
  }

  public static Worklog fromBusinessUser(@NonNull BusinessUser businessUser, @NonNull OffsetDateTime clockIn) {
    return fromBusinessUser(businessUser, clockIn, null);
  }

  public static Worklog fromBusinessUser(BusinessUser businessUser, OffsetDateTime clockIn, OffsetDateTime clockOut) {
    return Worklog.builder()
        .user(businessUser.user())
        .business(businessUser.business())
        .clockIn(clockIn)
        .clockOut(clockOut)
        .createdAt(Instant.now())
        .build()
        .toBusinessZone();
  }

}

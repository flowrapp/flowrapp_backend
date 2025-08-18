package io.github.flowrapp.model;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

@Builder(toBuilder = true)
@With
public record Worklog(
    Integer id,
    User user,
    Business business,
    Instant clockIn,
    Instant clockOut,
    Instant createdAt) {

  public boolean isOwner(@NonNull User user) {
    return this.user.id().equals(user.id());
  }

  public boolean isValid() {
    return clockIn != null && clockOut != null && clockIn.isBefore(clockOut);
  }

  public boolean isOpen() {
    return clockIn != null && clockOut == null;
  }

  public boolean isClosed() {
    return !isOpen();
  }

  public static Worklog fromBusinessUser(@NonNull BusinessUser businessUser, @NonNull Instant clockIn) {
    return fromBusinessUser(businessUser, clockIn, null);
  }

  public static Worklog fromBusinessUser(BusinessUser businessUser, Instant clockIn, Instant clockOut) {
    return Worklog.builder()
        .user(businessUser.user())
        .business(businessUser.business())
        .clockIn(clockIn)
        .clockOut(clockOut)
        .createdAt(Instant.now())
        .build();
  }

}

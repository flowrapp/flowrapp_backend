package io.github.flowrapp.model;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

@Builder(toBuilder = true)
@With
public record Worklog(
    Integer id,
    User user,
    Business business,
    OffsetDateTime clockIn,
    OffsetDateTime clockOut,
    OffsetDateTime createdAt) {

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

  public static Worklog fromBusinessUser(@NonNull BusinessUser businessUser, @NonNull OffsetDateTime clockIn) {
    return fromBusinessUser(businessUser, clockIn, null);
  }

  public static Worklog fromBusinessUser(BusinessUser businessUser, OffsetDateTime clockIn, OffsetDateTime clockOut) {
    return Worklog.builder()
        .user(businessUser.user())
        .business(businessUser.business())
        .clockIn(clockIn)
        .clockOut(clockOut)
        .createdAt(OffsetDateTime.now())
        .build();
  }

}

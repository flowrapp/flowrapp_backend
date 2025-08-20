package io.github.flowrapp.value;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import io.github.flowrapp.utils.DateUtils;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record WorklogFilteredRequest(
    Integer userId,
    Integer businessId,
    OffsetDateTime from,
    OffsetDateTime to,
    OffsetDateTime date) {

  public WorklogFilteredRequest truncate(ZoneId zoneOffset) {
    var offsetDate = date != null ? DateUtils.toZone.apply(date, zoneOffset) : null;

    return this.toBuilder()
        .from(
            Optional.ofNullable(offsetDate)
                .map(DateUtils.atStartOfDay)
                .orElseGet(() -> DateUtils.toZone.apply(from, zoneOffset)))
        .to(
            Optional.ofNullable(offsetDate)
                .map(DateUtils.atEndOfDay)
                .or(() -> Optional.ofNullable(to)
                    .map(DateUtils.toZone(zoneOffset)))
                .orElse(null))
        .build();
  }

}

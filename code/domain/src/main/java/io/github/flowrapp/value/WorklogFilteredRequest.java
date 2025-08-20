package io.github.flowrapp.value;

import java.time.OffsetDateTime;
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

  public WorklogFilteredRequest truncate() {
    return this.toBuilder()
        .from(
            Optional.ofNullable(date)
                .map(DateUtils.atStartOfDay)
                .orElse(from))
        .to(
            Optional.ofNullable(date)
                .map(DateUtils.atEndOfDay)
                .or(() -> Optional.ofNullable(to))
                .orElse(null))
        .build();
  }

}

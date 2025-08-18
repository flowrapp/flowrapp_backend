package io.github.flowrapp.model.value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import lombok.Builder;
import lombok.With;

@Builder(toBuilder = true)
@With
public record WorklogFilteredRequest(
    Integer userId,
    Integer businessId,
    Instant from,
    Instant to,
    Instant date) {

  public static long SECONDS_IN_DAY = 24 * 60 * 60L;

  public WorklogFilteredRequest truncate() {
    return this.toBuilder()
        .from(date != null ? date.truncatedTo(ChronoUnit.DAYS) : from)
        .to(
            Optional.ofNullable(date)
                .map(d -> d.truncatedTo(ChronoUnit.DAYS).plusSeconds(SECONDS_IN_DAY - 1))
                .or(() -> Optional.ofNullable(to))
                .orElse(null))
        .build();
  }

}

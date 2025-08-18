package io.github.flowrapp.model.value;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder(toBuilder = true)
public record DayHoursPair(
    LocalDate day,
    BigDecimal hours) {

}

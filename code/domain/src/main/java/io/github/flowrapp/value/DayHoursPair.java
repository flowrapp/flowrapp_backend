package io.github.flowrapp.value;

import java.time.LocalDate;

import io.github.flowrapp.model.Seconds;

import lombok.Builder;

@Builder(toBuilder = true)
public record DayHoursPair(
    LocalDate day,
    Seconds seconds) {

}

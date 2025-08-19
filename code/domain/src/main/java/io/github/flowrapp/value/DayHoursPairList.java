package io.github.flowrapp.value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record DayHoursPairList(
    LinkedHashMap<LocalDate, BigDecimal> hours) {

  public static DayHoursPairList of(LocalDate date, BigDecimal hours) {
    return new DayHoursPairList(
        new LinkedHashMap<>(Map.of(date, hours)));
  }

  public DayHoursPairList fill(LocalDate from, LocalDate to) {
    LinkedHashMap<LocalDate, BigDecimal> filledHours = new LinkedHashMap<>();
    LocalDate current = from;

    while (!current.isAfter(to)) {
      filledHours.put(current, hours.getOrDefault(current, BigDecimal.ZERO));
      current = current.plusDays(1);
    }

    return new DayHoursPairList(filledHours);
  }

  public DayHoursPairList merge(DayHoursPairList other) {
    LinkedHashMap<LocalDate, BigDecimal> mergedHours = new LinkedHashMap<>(this.hours);

    other.hours.forEach((day, hours) -> mergedHours.merge(day, hours, BigDecimal::add));

    return new DayHoursPairList(mergedHours);
  }

  public List<DayHoursPair> getlist() {
    return hours.entrySet().stream()
        .map(entry -> new DayHoursPair(entry.getKey(), entry.getValue()))
        .toList();
  }

}

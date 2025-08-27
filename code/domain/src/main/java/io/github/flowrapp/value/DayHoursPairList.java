package io.github.flowrapp.value;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.flowrapp.model.Seconds;

public record DayHoursPairList(
    LinkedHashMap<LocalDate, Seconds> hours) {

  public static DayHoursPairList of(LocalDate date, Seconds hours) {
    return new DayHoursPairList(
        new LinkedHashMap<>(Map.of(date, hours)));
  }

  public DayHoursPairList fill(LocalDate from, LocalDate to) {
    LinkedHashMap<LocalDate, Seconds> filledHours = new LinkedHashMap<>();
    LocalDate current = from;

    while (!current.isAfter(to)) {
      filledHours.put(current, hours.getOrDefault(current, Seconds.ZERO));
      current = current.plusDays(1);
    }

    return new DayHoursPairList(filledHours);
  }

  public DayHoursPairList merge(DayHoursPairList other) {
    LinkedHashMap<LocalDate, Seconds> mergedHours = new LinkedHashMap<>(this.hours);

    other.hours.forEach((day, hours) -> mergedHours.merge(day, hours, Seconds::add));

    return new DayHoursPairList(mergedHours);
  }

  public List<DayHoursPair> getlist() {
    return hours.entrySet().stream()
        .map(entry -> new DayHoursPair(entry.getKey(), entry.getValue()))
        .toList();
  }

}

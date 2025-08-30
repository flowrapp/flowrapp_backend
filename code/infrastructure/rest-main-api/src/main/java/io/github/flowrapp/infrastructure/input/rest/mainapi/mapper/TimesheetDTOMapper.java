package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import static java.util.stream.Collectors.toMap;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.flowrapp.infrastructure.apirest.users.model.GetWeeklyHoursReport200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetWeeklyHoursReport200ResponseUsersInnerDTO;
import io.github.flowrapp.model.Seconds;
import io.github.flowrapp.value.DayHoursPairList;
import io.github.flowrapp.value.TimesheetFilterRequest;
import io.github.flowrapp.value.UserTimeReportSummary;

import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.threeten.extra.YearWeek;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TimesheetDTOMapper {

  @Mapping(target = "from", expression = "java(mapWeek2from(week))")
  @Mapping(target = "to", expression = "java(mapWeek2to(week))")
  TimesheetFilterRequest rest2domain(Integer userId, Integer businessId, @NotNull String week);

  @Mapping(target = "startDate", expression = "java(mapWeek2from(week))")
  @Mapping(target = "endDate", expression = "java(mapWeek2to(week))")
  @Mapping(target = "users", source = "response")
  GetWeeklyHoursReport200ResponseDTO domain2rest(Integer businessId, String week, List<UserTimeReportSummary> response);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "username", source = "user.name")
  @Mapping(target = "totalHours", source = "totalSeconds")
  @Mapping(target = "dailyHours", source = "dailySeconds")
  GetWeeklyHoursReport200ResponseUsersInnerDTO map(UserTimeReportSummary summary);

  default Map<String, String> map(DayHoursPairList dayHoursPairs) {
    if (dayHoursPairs == null) {
      return Map.of();
    }

    return dayHoursPairs.getlist().stream()
        .collect(toMap(
            p -> p.day().toString(),
            p -> p.seconds().formatted(),
            (first, ignored) -> first, // Duplicate keys should not happen
            LinkedHashMap::new)); // Maintain insertion order
  }

  default String map(Seconds seconds) {
    return seconds != null ? seconds.formatted() : null;
  }

  default LocalDate mapWeek2from(String week) {
    return YearWeek.parse(week).atDay(DayOfWeek.MONDAY);
  }

  default LocalDate mapWeek2to(String week) {
    return YearWeek.parse(week).atDay(DayOfWeek.SUNDAY);
  }

}

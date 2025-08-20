package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import static java.util.stream.Collectors.toMap;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.flowrapp.infrastructure.apirest.users.model.GetWeeklyHoursReport200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetWeeklyHoursReport200ResponseUsersInnerDTO;
import io.github.flowrapp.value.DayHoursPairList;
import io.github.flowrapp.value.TimesheetFilterRequest;
import io.github.flowrapp.value.UserTimeReportSummary;

import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.threeten.extra.YearWeek;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
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
  GetWeeklyHoursReport200ResponseUsersInnerDTO map(UserTimeReportSummary summary);

  default Map<String, Double> map(DayHoursPairList dayHoursPairs) {
    return dayHoursPairs.getlist().stream()
        .collect(toMap(dp -> dp.day().toString(),
            dp -> dp.hours().doubleValue(),
            Double::sum, // In case of duplicate days, sum the times
            LinkedHashMap::new)); // Maintain insertion order
  }

  default LocalDate mapWeek2from(String week) {
    return YearWeek.parse(week).atDay(DayOfWeek.MONDAY);
  }

  default LocalDate mapWeek2to(String week) {
    return YearWeek.parse(week).atDay(DayOfWeek.SUNDAY);
  }

}

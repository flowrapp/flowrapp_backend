package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.ClockIn200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockInRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockOutRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.UpdateWorklogRequestDTO;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.value.WorklogClockInRequest;
import io.github.flowrapp.value.WorklogClockOutRequest;
import io.github.flowrapp.value.WorklogFilteredRequest;
import io.github.flowrapp.value.WorklogUpdateRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = SPRING,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface WorklogDTOMapper {

  @Mapping(target = "clockIn", source = "clockInRequestDTO.clockIn", defaultExpression = "java(java.time.OffsetDateTime.now())")
  WorklogClockInRequest rest2domain(Long businessId, ClockInRequestDTO clockInRequestDTO);

  @Mapping(target = "clockOut", source = "clockOutRequestDTO.clockOut", defaultExpression = "java(java.time.OffsetDateTime.now())")
  WorklogClockOutRequest rest2domain(Long worklogId, Long businessId, ClockOutRequestDTO clockOutRequestDTO);

  WorklogUpdateRequest rest2domain(Long worklogId, UpdateWorklogRequestDTO updateWorklogRequestDTO);

  WorklogFilteredRequest rest2domain(Long userId, Long businessId, LocalDate from, LocalDate to, LocalDate date);

  @Mapping(target = "userId", ignore = true)
  WorklogFilteredRequest rest2domain(Long businessId, LocalDate from, LocalDate to, LocalDate date);

  @Mapping(target = "userId", source = "user.id")
  ClockIn200ResponseDTO domain2rest(Worklog result);

  List<ClockIn200ResponseDTO> domain2rest(List<Worklog> worklogs);

  default OffsetDateTime map(LocalDate date) {
    return date != null ? date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime() : null;
  }

}

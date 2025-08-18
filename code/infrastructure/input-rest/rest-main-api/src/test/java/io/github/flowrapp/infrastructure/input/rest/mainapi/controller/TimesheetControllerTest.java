package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.GetWeeklyHoursReport200ResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.TimesheetDTOMapper;
import io.github.flowrapp.model.value.TimesheetFilterRequest;
import io.github.flowrapp.model.value.UserTimeReportSummary;
import io.github.flowrapp.port.input.TimesheetUseCase;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.threeten.extra.YearWeek;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class TimesheetControllerTest {

  @Mock
  private TimesheetUseCase timesheetUseCase;

  @Spy
  private TimesheetDTOMapper timesheetDTOMapper = Mappers.getMapper(TimesheetDTOMapper.class);

  @InjectMocks
  private TimesheetController timesheetController;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getWeeklyHoursReport(Integer businessId, Integer userId, UserTimeReportSummary response) {
    // GIVEN
    String week = "2025-W01";
    when(timesheetUseCase.getSummaryReport(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(businessId, TimesheetFilterRequest::businessId)
        .returns(userId, TimesheetFilterRequest::userId)
        .returns(LocalDate.parse("2024-12-30"), TimesheetFilterRequest::from)
        .returns(LocalDate.parse("2025-01-05"), TimesheetFilterRequest::to))))
            .thenReturn(List.of(response));

    // WHEN
    var result = timesheetController.getWeeklyHoursReport(String.valueOf(businessId), week, String.valueOf(userId));

    // THEN
    assertThat(result)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(String.valueOf(businessId), GetWeeklyHoursReport200ResponseDTO::getBusinessId)
        .returns(week, GetWeeklyHoursReport200ResponseDTO::getWeek)
        .returns(YearWeek.parse(week).atDay(DayOfWeek.MONDAY), GetWeeklyHoursReport200ResponseDTO::getStartDate)
        .returns(YearWeek.parse(week).atDay(DayOfWeek.SUNDAY), GetWeeklyHoursReport200ResponseDTO::getEndDate)
        .extracting(GetWeeklyHoursReport200ResponseDTO::getUsers)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(1)
        .singleElement()
        .isNotNull();
  }
}

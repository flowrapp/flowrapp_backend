package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.ClockIn200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockInRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockOutRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.UpdateWorklogRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.WorklogDTOMapper;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.model.value.WorklogClockInRequest;
import io.github.flowrapp.model.value.WorklogClockOutRequest;
import io.github.flowrapp.model.value.WorklogFilteredRequest;
import io.github.flowrapp.model.value.WorklogUpdateRequest;
import io.github.flowrapp.port.input.WorklogUseCase;

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

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class WorklogsControllerTest {

  @Mock
  private WorklogUseCase worklogUseCase;

  @Spy
  private WorklogDTOMapper worklogDTOMapper = Mappers.getMapper(WorklogDTOMapper.class);

  @InjectMocks
  private WorklogsController worklogsController;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockIn(Long businessId, ClockInRequestDTO clockInRequestDTO, Worklog worklog) {
    // GIVEN
    when(worklogUseCase.clockIn(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(businessId.intValue(), WorklogClockInRequest::businessId)
        .returns(clockInRequestDTO.getClockIn(), WorklogClockInRequest::clockIn))))
            .thenReturn(worklog);

    // WHEN
    var response = worklogsController.clockIn(businessId, clockInRequestDTO);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .returns(Long.valueOf(worklog.id()), ClockIn200ResponseDTO::getId)
        .returns(Long.valueOf(worklog.user().id()), ClockIn200ResponseDTO::getUserId)
        .returns(worklog.clockIn(), ClockIn200ResponseDTO::getClockIn)
        .returns(worklog.clockOut(), ClockIn200ResponseDTO::getClockOut);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void clockOut(Long businessId, Long worklogId, ClockOutRequestDTO clockOutRequestDTO, Worklog worklog) {
    // GIVEN
    when(worklogUseCase.clockOut(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(businessId.intValue(), WorklogClockOutRequest::businessId)
        .returns(worklogId.intValue(), WorklogClockOutRequest::worklogId)
        .returns(clockOutRequestDTO.getClockOut(), WorklogClockOutRequest::clockOut))))
            .thenReturn(worklog);

    // WHEN
    var response = worklogsController.clockOut(businessId, worklogId, clockOutRequestDTO);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void updateWorklog(Long worklogId, UpdateWorklogRequestDTO updateWorklogRequestDTO, Worklog worklog) {
    // GIVEN
    when(worklogUseCase.updateWorklog(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(worklogId.intValue(), WorklogUpdateRequest::worklogId)
        .returns(updateWorklogRequestDTO.getClockIn(), WorklogUpdateRequest::clockIn)
        .returns(updateWorklogRequestDTO.getClockOut(), WorklogUpdateRequest::clockOut))))
            .thenReturn(worklog);

    // WHEN
    var response = worklogsController.updateWorklog(worklogId, updateWorklogRequestDTO);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getWorklogById(Long worklogId, Worklog worklog) {
    // GIVEN
    when(worklogUseCase.getById(worklogId.intValue()))
        .thenReturn(worklog);

    // WHEN
    var response = worklogsController.getWorklogById(worklogId);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserWorklogs(LocalDate from, LocalDate to, LocalDate date, Long businessId, List<Worklog> worklogs) {
    // GIVEN
    when(worklogUseCase.getUserWorklogs(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(null, WorklogFilteredRequest::userId)
        .returns(businessId.intValue(), WorklogFilteredRequest::businessId)
        .returns(from.atStartOfDay().atOffset(OffsetDateTime.now().getOffset()), WorklogFilteredRequest::from)
        .returns(to.atStartOfDay().atOffset(OffsetDateTime.now().getOffset()), WorklogFilteredRequest::to)
        .returns(date.atStartOfDay().atOffset(OffsetDateTime.now().getOffset()), WorklogFilteredRequest::date))))
            .thenReturn(worklogs);

    // WHEN
    var response = worklogsController.getUserWorklogs(from, to, date, businessId);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(worklogs.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getWorklogs(Long businessId, LocalDate from, LocalDate to,
      LocalDate date, Long userId, List<Worklog> worklogs) {
    // GIVEN
    when(worklogUseCase.getBusinessWorklogs(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(userId.intValue(), WorklogFilteredRequest::userId)
        .returns(businessId.intValue(), WorklogFilteredRequest::businessId)
        .returns(from.atStartOfDay().atOffset(OffsetDateTime.now().getOffset()), WorklogFilteredRequest::from)
        .returns(to.atStartOfDay().atOffset(OffsetDateTime.now().getOffset()), WorklogFilteredRequest::to)
        .returns(date.atStartOfDay().atOffset(OffsetDateTime.now().getOffset()), WorklogFilteredRequest::date))))
            .thenReturn(worklogs);

    // WHEN
    var response = worklogsController.getWorklogs(businessId, from, to, date, userId);

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(worklogs.size());
  }
}

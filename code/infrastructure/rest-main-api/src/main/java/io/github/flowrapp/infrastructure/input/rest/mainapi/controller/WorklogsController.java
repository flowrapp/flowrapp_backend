package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import java.time.LocalDate;
import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.api.WorklogsApi;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockIn200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockInRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.ClockOutRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.UpdateWorklogRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.WorklogDTOMapper;
import io.github.flowrapp.port.input.WorklogUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WorklogsController implements WorklogsApi {

  private final WorklogUseCase worklogUseCase;

  private final WorklogDTOMapper worklogDTOMapper;

  @Override
  public ResponseEntity<ClockIn200ResponseDTO> clockIn(Long businessId, ClockInRequestDTO clockInRequestDTO) {
    val result = worklogUseCase.clockIn(
        worklogDTOMapper.rest2domain(businessId, clockInRequestDTO));

    return ResponseEntity.ok(
        worklogDTOMapper.domain2rest(result));
  }

  @Override
  public ResponseEntity<ClockIn200ResponseDTO> clockOut(Long businessId, Long worklogId, ClockOutRequestDTO clockOutRequestDTO) {
    val result = worklogUseCase.clockOut(
        worklogDTOMapper.rest2domain(worklogId, businessId, clockOutRequestDTO));

    return ResponseEntity.ok(
        worklogDTOMapper.domain2rest(result));
  }

  @Override
  public ResponseEntity<ClockIn200ResponseDTO> updateWorklog(Long worklogId, UpdateWorklogRequestDTO updateWorklogRequestDTO) {
    val result = worklogUseCase.updateWorklog(
        worklogDTOMapper.rest2domain(worklogId, updateWorklogRequestDTO));

    return ResponseEntity.ok(
        worklogDTOMapper.domain2rest(result));
  }

  @Override
  public ResponseEntity<ClockIn200ResponseDTO> getWorklogById(Long worklogId) {
    return ResponseEntity.ok(
        worklogDTOMapper.domain2rest(
            worklogUseCase.getById(worklogId.intValue())));
  }

  @Override
  public ResponseEntity<List<ClockIn200ResponseDTO>> getUserWorklogs(LocalDate from, LocalDate to, LocalDate date, Long businessId) {
    val result = worklogUseCase.getUserWorklogs(
        worklogDTOMapper.rest2domain(businessId, from, to, date));

    return ResponseEntity.ok(
        worklogDTOMapper.domain2rest(result));
  }

  @Override
  public ResponseEntity<List<ClockIn200ResponseDTO>> getWorklogs(Long businessId, LocalDate from, LocalDate to,
      LocalDate date, Long userId) {
    val result = worklogUseCase.getBusinessWorklogs(
        worklogDTOMapper.rest2domain(userId, businessId, from, to, date));

    return ResponseEntity.ok(
        worklogDTOMapper.domain2rest(result));
  }

}

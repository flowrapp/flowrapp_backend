package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import io.github.flowrapp.infrastructure.apirest.users.api.TimesheetApi;
import io.github.flowrapp.infrastructure.apirest.users.model.GetWeeklyHoursReport200ResponseDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.TimesheetDTOMapper;
import io.github.flowrapp.port.input.TimesheetUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimesheetController implements TimesheetApi {

  private final TimesheetUseCase timesheetUseCase;

  private final TimesheetDTOMapper timesheetDTOMapper;

  @Override
  public ResponseEntity<GetWeeklyHoursReport200ResponseDTO> getWeeklyHoursReport(String businessId, String week, String userId) {
    val response = timesheetUseCase.getSummaryReport(
        timesheetDTOMapper.rest2domain(userId, businessId, week));

    return ResponseEntity.ok(
        timesheetDTOMapper.domain2rest(businessId, week, response));
  }

}

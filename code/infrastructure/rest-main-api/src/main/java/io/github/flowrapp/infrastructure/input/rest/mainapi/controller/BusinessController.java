package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.api.BusinessesApi;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessUsers200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserBusinesses200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.BusinessDTOMapper;
import io.github.flowrapp.port.input.BusinessUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BusinessController implements BusinessesApi {

  private final BusinessUseCase businessUseCase;

  private final BusinessDTOMapper businessDTOMapper;

  @Override
  public ResponseEntity<List<GetBusinessUsers200ResponseInnerDTO>> getBusinessUsers(Integer businessId, String role) {
    val response = businessUseCase.getBusinessUsers(
        businessDTOMapper.rest2domain(businessId, role));

    return ResponseEntity.ok(
        businessDTOMapper.domain2restUsers(response));
  }

  @Override
  public ResponseEntity<List<GetUserBusinesses200ResponseInnerDTO>> getUserBusinesses() {
    return ResponseEntity.ok(
        businessDTOMapper.domain2rest(
            businessUseCase.getUserBusiness()));
  }
}

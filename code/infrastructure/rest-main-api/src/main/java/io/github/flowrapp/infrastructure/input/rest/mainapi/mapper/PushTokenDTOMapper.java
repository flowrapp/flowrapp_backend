package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import io.github.flowrapp.infrastructure.apirest.users.model.RegisterPushTokenRequestDTO;
import io.github.flowrapp.value.PushTokenRequest;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PushTokenDTOMapper {

  PushTokenRequest rest2domain(RegisterPushTokenRequestDTO registerPushTokenRequestDTO);

}

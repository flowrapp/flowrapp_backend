package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import io.github.flowrapp.infrastructure.apirest.users.model.Login200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.LoginRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RefreshTokenRequestDTO;
import io.github.flowrapp.model.LoginRequest;
import io.github.flowrapp.model.RefreshRequest;
import io.github.flowrapp.model.TokensResponse;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface AuthDTOMapper {

  LoginRequest rest2domain(LoginRequestDTO loginRequestDTO);

  RefreshRequest rest2domain(RefreshTokenRequestDTO refreshTokenRequestDTO);

  Login200ResponseDTO domain2rest(TokensResponse tokensResponse);

}

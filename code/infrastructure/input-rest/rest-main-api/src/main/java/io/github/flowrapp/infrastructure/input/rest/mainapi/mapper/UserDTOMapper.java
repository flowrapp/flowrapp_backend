package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.github.flowrapp.infrastructure.apirest.users.model.GetUser200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserRequestDTO;
import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.value.MockUserRequest;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = SPRING)
public interface UserDTOMapper {

  MockUserRequest infra2domain(GetUserRequestDTO userRequestDTO);

  GetUser200ResponseDTO domain2infra(MockUser result);

}

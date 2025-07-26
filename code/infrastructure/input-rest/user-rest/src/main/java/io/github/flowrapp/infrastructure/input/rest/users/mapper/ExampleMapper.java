package io.github.flowrapp.infrastructure.input.rest.users.mapper;

import io.github.flowrapp.infrastructure.apirest.users.model.GetUser200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserRequestDTO;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRequest;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExampleMapper {

  UserRequest infra2domain(GetUserRequestDTO userRequestDTO);

  GetUser200ResponseDTO domain2infra(User result);

}

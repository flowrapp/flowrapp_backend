package io.github.flowrapp.infrastructure.input.rest.users.mapper;

import io.github.flowrapp.infrastructure.input.rest.users.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.users.dto.UserResponseDTO;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRequest;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExampleMapper {

  UserRequest infra2domain(UserRequestDTO userRequestDTO);

  UserResponseDTO domain2infra(User result);

}

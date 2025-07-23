package io.github.flowrapp.infrastructure.input.rest.example.mapper;

import io.github.flowrapp.infrastructure.input.rest.example.dto.UserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.example.dto.UserResponseDTO;
import io.github.flowrapp.model.User;
import io.github.flowrapp.model.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExampleMapper {

  UserRequest infra2domain(UserRequestDTO userRequestDTO);

  UserResponseDTO domain2infra(User result);

}

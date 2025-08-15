package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;

import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.AdminDTOMapper;
import io.github.flowrapp.port.input.AdminUseCase;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class AdminControllerTest {

  @Mock
  private AdminUseCase adminUseCase;

  @Spy
  private AdminDTOMapper adminDTOMapper = Mappers.getMapper(AdminDTOMapper.class);

  @InjectMocks
  private AdminController adminController;

  @ParameterizedTest
  @InstancioSource
  void registerUser(RegisterUserRequestDTO registerUserRequestDTO) {
    // Given

    // When
    adminController.registerUser(registerUserRequestDTO);

    // Then
    verify(adminUseCase).createUser(assertArg(argument -> {
      assertNotNull(argument);
      assertEquals(registerUserRequestDTO.getUsername(), argument.username());
      assertEquals(registerUserRequestDTO.getMail(), argument.mail());

      var argBusiness = argument.business();
      var dtoBusinesses = registerUserRequestDTO.getBusiness().getFirst();

      assertNotNull(argBusiness);
      assertEquals(dtoBusinesses.getName(), argBusiness.name());

      var location = argBusiness.location();
      var dtoLocation = dtoBusinesses.getLocation();
      assertNotNull(location);
      assertEquals(dtoLocation.getLatitude(), location.latitude());
      assertEquals(dtoLocation.getLongitude(), location.longitude());
      assertEquals(dtoLocation.getArea(), location.area());
    }));
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void registerUserWithNullBusiness(RegisterUserRequestDTO registerUserRequestDTO) {
    // Given
    registerUserRequestDTO.setBusiness(null);

    // When
    adminController.registerUser(registerUserRequestDTO);

    // Then
    verify(adminUseCase).createUser(assertArg(argument -> {
      assertNotNull(argument);
      assertEquals(registerUserRequestDTO.getUsername(), argument.username());
      assertEquals(registerUserRequestDTO.getMail(), argument.mail());
      assertNull(argument.business());
    }));
  }

}

package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessUsers200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserBusinesses200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.input.rest.mainapi.mapper.BusinessDTOMapper;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.UserRole;
import io.github.flowrapp.port.input.BusinessUseCase;
import io.github.flowrapp.value.BusinessFilterRequest;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class BusinessControllerTest {

  @Mock
  private BusinessUseCase businessUseCase;

  @Spy
  private BusinessDTOMapper businessDTOMapper = Mappers.getMapper(BusinessDTOMapper.class);

  @InjectMocks
  private BusinessController businessController;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessUsers(Integer businessId, UserRole role, BusinessUser businessUser) {
    // GIVEN
    when(businessUseCase.getBusinessUsers(assertArg(filter -> assertThat(filter)
        .isNotNull()
        .returns(businessId, BusinessFilterRequest::businessId)
        .returns(role, BusinessFilterRequest::role))))
            .thenReturn(List.of(businessUser));

    // WHEN
    var response = businessController.getBusinessUsers(businessId, role.name());

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(1)
        .singleElement()
        .asInstanceOf(type(GetBusinessUsers200ResponseInnerDTO.class))
        .returns(businessUser.user().id(), GetBusinessUsers200ResponseInnerDTO::getUserId)
        .returns(businessUser.user().name(), GetBusinessUsers200ResponseInnerDTO::getUsername)
        .returns(businessUser.role().toString(), dto -> dto.getRole().toString());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserBusinesses(BusinessUser businessUser) {
    // GIVEN
    when(businessUseCase.getUserBusiness())
        .thenReturn(List.of(businessUser));

    // WHEN
    var response = businessController.getUserBusinesses();

    // THEN
    assertThat(response)
        .isNotNull()
        .returns(OK, ResponseEntity::getStatusCode)
        .extracting(ResponseEntity::getBody)
        .isNotNull()
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .hasSize(1)
        .singleElement()
        .asInstanceOf(type(GetUserBusinesses200ResponseInnerDTO.class))
        .returns(Long.valueOf(businessUser.business().id()), GetUserBusinesses200ResponseInnerDTO::getId)
        .returns(businessUser.business().name(), GetUserBusinesses200ResponseInnerDTO::getName)
        .returns(businessUser.business().zone().getId(), GetUserBusinesses200ResponseInnerDTO::getZone)
        .returns(businessUser.role().toString(), dto -> dto.getUserRole().toString())
        .returns(businessUser.business().location().latitude(), dto -> dto.getLocation().getLatitude())
        .returns(businessUser.business().location().longitude(), dto -> dto.getLocation().getLongitude())
        .returns(businessUser.business().location().area(), dto -> dto.getLocation().getArea());
  }

}

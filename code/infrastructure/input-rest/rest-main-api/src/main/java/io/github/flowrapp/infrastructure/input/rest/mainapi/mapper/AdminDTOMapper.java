package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestBusinessInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestBusinessInnerLocationDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserRequestDTO;
import io.github.flowrapp.model.Location;
import io.github.flowrapp.model.value.BusinessCreationRequest;
import io.github.flowrapp.model.value.UserCreationRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface AdminDTOMapper {

  @Mapping(target = "business", source = "business", qualifiedByName = "businessListToSinglesDomain")
  UserCreationRequest rest2domain(RegisterUserRequestDTO registerUserRequestDTO);

  @Named("businessListToSinglesDomain")
  default BusinessCreationRequest businessListToSinglesDomain(List<RegisterUserRequestBusinessInnerDTO> businessList) {
    if (businessList == null || businessList.isEmpty()) {
      return null;
    }

    return rest2domain(businessList.getFirst());
  }

  BusinessCreationRequest rest2domain(RegisterUserRequestBusinessInnerDTO businessCreationRequestDTO);

  Location rest2domain(RegisterUserRequestBusinessInnerLocationDTO locationDTO);

}

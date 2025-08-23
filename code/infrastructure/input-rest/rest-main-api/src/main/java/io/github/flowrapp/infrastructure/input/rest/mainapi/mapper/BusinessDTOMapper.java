package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import java.time.ZoneId;
import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessUsers200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetUserBusinesses200ResponseInnerDTO;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.value.BusinessFilterRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BusinessDTOMapper {

  @Mapping(target = "userId", ignore = true)
  BusinessFilterRequest rest2domain(Integer businessId, String role);

  @Mapping(target = "id", source = "business.id")
  @Mapping(target = "name", source = "business.name")
  @Mapping(target = "zone", source = "business.zone")
  @Mapping(target = "userRole", source = "role")
  @Mapping(target = "location.latitude", source = "business.location.latitude")
  @Mapping(target = "location.longitude", source = "business.location.longitude")
  @Mapping(target = "location.area", source = "business.location.area")
  GetUserBusinesses200ResponseInnerDTO domain2restUser(BusinessUser businesses);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "username", source = "user.name")
  GetBusinessUsers200ResponseInnerDTO domain2rest(BusinessUser userBusinesses);

  List<GetUserBusinesses200ResponseInnerDTO> domain2rest(List<BusinessUser> businesses);

  List<GetBusinessUsers200ResponseInnerDTO> domain2restUsers(List<BusinessUser> userBusinesses);

  default String map(ZoneId value) {
    return value == null ? null : value.getId();
  }

}

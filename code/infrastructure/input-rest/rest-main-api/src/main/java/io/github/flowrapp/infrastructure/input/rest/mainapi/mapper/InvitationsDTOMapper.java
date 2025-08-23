package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserFromInvitationRequestDTO;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.value.InvitationCreationRequest;
import io.github.flowrapp.value.InvitationRegistrationRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InvitationsDTOMapper {

  @Mapping(target = "email", source = "invited.mail")
  GetBusinessInvitations200ResponseInnerDTO domain2rest(Invitation result);

  List<GetBusinessInvitations200ResponseInnerDTO> domain2rest(List<Invitation> result);

  @Mapping(target = "businessId", source = "businessId")
  @Mapping(target = "email", source = "createBusinessInvitationRequestDTO.email")
  @Mapping(target = "role", source = "createBusinessInvitationRequestDTO.role")
  InvitationCreationRequest rest2domain(Integer businessId,
      CreateBusinessInvitationRequestDTO createBusinessInvitationRequestDTO);

  InvitationRegistrationRequest rest2domain(String token, RegisterUserFromInvitationRequestDTO registerDTO);

  default OffsetDateTime map(Instant date) {
    return date != null ? date.atOffset(ZoneOffset.UTC) : null;
  }

}

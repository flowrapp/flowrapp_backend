package io.github.flowrapp.infrastructure.input.rest.mainapi.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.List;

import io.github.flowrapp.infrastructure.apirest.users.model.AcceptInvitation200ResponseDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.CreateBusinessInvitationRequestDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.GetBusinessInvitations200ResponseInnerDTO;
import io.github.flowrapp.infrastructure.apirest.users.model.RegisterUserFromInvitationRequestDTO;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.value.InvitationCreationRequest;
import io.github.flowrapp.model.value.InvitationRegistrationRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = SPRING)
public interface InvitationsDTOMapper {

  @Mapping(target = "businessId", source = "business.id")
  @Mapping(target = "invitationId", source = "id")
  AcceptInvitation200ResponseDTO domain2restAccept(Invitation result);

  @Mapping(target = "email", source = "invited.mail")
  GetBusinessInvitations200ResponseInnerDTO domain2rest(Invitation result);

  List<GetBusinessInvitations200ResponseInnerDTO> domain2rest(List<Invitation> result);

  @Mapping(target = "businessId", source = "businessId")
  @Mapping(target = "email", source = "createBusinessInvitationRequestDTO.email")
  @Mapping(target = "role", source = "createBusinessInvitationRequestDTO.role")
  InvitationCreationRequest rest2domain(Integer businessId,
      CreateBusinessInvitationRequestDTO createBusinessInvitationRequestDTO);

  InvitationRegistrationRequest rest2domain(String token, RegisterUserFromInvitationRequestDTO registerDTO);
}

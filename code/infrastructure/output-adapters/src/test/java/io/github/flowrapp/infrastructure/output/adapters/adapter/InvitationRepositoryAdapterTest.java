package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.InvitationJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.InvitationEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.model.UserRole;

import lombok.val;
import org.instancio.Instancio;
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
class InvitationRepositoryAdapterTest {

  @Mock
  private InvitationJpaRepository invitationJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

  @Spy
  @InjectMocks
  private BusinessEntityMapper businessEntityMapper = spy(Mappers.getMapper(BusinessEntityMapper.class));

  @Spy
  @InjectMocks
  private InvitationEntityMapper invitationEntityMapper = spy(Mappers.getMapper(InvitationEntityMapper.class));

  @InjectMocks
  private InvitationRepositoryAdapter invitationRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource
  void findByToken(UUID token) {
    // Given
    val invitationEntity = this.generateInvitationEntity();

    when(invitationJpaRepository.findByToken(token))
        .thenReturn(Optional.of(invitationEntity));

    // When
    val result = invitationRepositoryAdapter.findByToken(token);

    // Then
    assertThat(result)
        .isPresent()
        .get()
        .satisfies(invitation -> {
          assertNotNull(invitation.invited());
          assertEquals(invitation.invited().name(), invitationEntity.getInvited().getName());
        })
        .satisfies(invitation -> {
          assertNotNull(invitation.invitedBy());
          assertEquals(invitation.invitedBy().name(), invitationEntity.getInvitedBy().getName());
        })
        .satisfies(invitation -> {
          assertNotNull(invitation.business());
          assertEquals(invitation.business().name(), invitationEntity.getBusiness().getName());
        })
        .returns(invitationEntity.getToken(), Invitation::token)
        .returns(invitationEntity.getRole(), invitation -> invitation.role().toString())
        .returns(invitationEntity.getCreatedAt(), Invitation::createdAt)
        .returns(invitationEntity.getExpiresAt(), Invitation::expiresAt)
        .returns(invitationEntity.getStatus(), invitation -> invitation.status().toString());
  }

  @ParameterizedTest
  @InstancioSource
  void save(Invitation invitationReq) {
    // Given
    val invitationEntity = this.generateInvitationEntity();

    when(invitationJpaRepository.save(argThat(argument -> argument.getToken().equals(invitationReq.token()))))
        .thenReturn(invitationEntity);

    // When
    val result = invitationRepositoryAdapter.save(invitationReq);

    // Then
    assertThat(result)
        .isNotNull()
        .satisfies(invitation -> {
          assertNotNull(invitation.invited());
          assertEquals(invitation.invited().name(), invitationEntity.getInvited().getName());
        })
        .satisfies(invitation -> {
          assertNotNull(invitation.invitedBy());
          assertEquals(invitation.invitedBy().name(), invitationEntity.getInvitedBy().getName());
        })
        .satisfies(invitation -> {
          assertNotNull(invitation.business());
          assertEquals(invitation.business().name(), invitationEntity.getBusiness().getName());
        })
        .returns(invitationEntity.getToken(), Invitation::token)
        .returns(invitationEntity.getRole(), invitation -> invitation.role().name())
        .returns(invitationEntity.getCreatedAt(), Invitation::createdAt)
        .returns(invitationEntity.getExpiresAt(), Invitation::expiresAt)
        .returns(invitationEntity.getStatus(), invitation -> invitation.status().name());
  }

  private InvitationEntity generateInvitationEntity() {
    return Instancio.of(InvitationEntity.class)
        .generate(field(InvitationEntity::getRole), gen -> gen.oneOf(UserRole.values()).asString())
        .generate(field(InvitationEntity::getStatus), gen -> gen.oneOf(InvitationStatus.values()).asString())
        .create();
  }

}

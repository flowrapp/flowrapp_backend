package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.InvitationJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.InvitationEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.BusinessUserRole;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;

import lombok.val;
import org.apache.commons.lang3.RandomUtils;
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
  @InstancioSource(samples = 20)
  void findById(Integer invitationId) {
    // Given
    val invitationEntity = this.generateInvitationEntity();

    when(invitationJpaRepository.findById(invitationId))
        .thenReturn(Optional.of(invitationEntity));

    // When
    val result = invitationRepositoryAdapter.findById(invitationId);

    // Then
    assertThat(result)
        .isPresent()
        .get()
        .returns(invitationEntity.getId(), Invitation::id);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
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
        .returns(invitationEntity.getId(), Invitation::id)
        .returns(invitationEntity.getToken(), Invitation::token)
        .returns(invitationEntity.getRole(), invitation -> invitation.role().toString())
        .returns(invitationEntity.getCreatedAt(), Invitation::createdAt)
        .returns(invitationEntity.getExpiresAt(), Invitation::expiresAt)
        .returns(invitationEntity.getStatus(), invitation -> invitation.status().toString());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void save(Invitation invitationReq) {
    // Given
    val invitationEntity = this.generateInvitationEntity();

    when(invitationJpaRepository.save(argThat(argument -> argument.getToken().equals(invitationReq.token()))))
        .thenReturn(invitationEntity);

    // When
    val result = invitationRepositoryAdapter.save(invitationReq);

    // Then
    assertThat(result)
        .isNotNull();
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void findByBusinessIdAndStatus(Integer businessId, InvitationStatus status) {
    // Given
    var invitationList = IntStream.range(0, RandomUtils.secure().randomInt(1, 10))
        .mapToObj(unused -> this.generateInvitationEntity())
        .toList();

    when(invitationJpaRepository.findAll())
        .thenReturn(invitationList);

    // When
    val result = invitationRepositoryAdapter.findByBusinessIdAndStatus(businessId, status);

    // Then
    assertThat(result)
        .isNotNull()
        .isNotEmpty()
        .hasSize(invitationList.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void findByUserAndStatus(Integer userId, InvitationStatus status) {
    // Given
    var invitationList = IntStream.range(0, RandomUtils.secure().randomInt(1, 10))
        .mapToObj(unused -> this.generateInvitationEntity())
        .toList();

    when(invitationJpaRepository.findAll())
        .thenReturn(invitationList);

    // When
    val result = invitationRepositoryAdapter.findByUserAndStatus(userId, status);

    // Then
    assertThat(result)
        .isNotNull()
        .isNotEmpty()
        .hasSize(invitationList.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void userIsAlreadyInvitedToBusiness(Integer invitedUserId, Integer businessId) {
    // Given
    when(invitationJpaRepository.existsByInvited_IdAndBusiness_IdAndStatusIs(invitedUserId, businessId, InvitationStatus.PENDING.name()))
        .thenReturn(true);

    // When
    val result = invitationRepositoryAdapter.userIsAlreadyInvitedToBusiness(invitedUserId, businessId);

    // Then
    assertThat(result).isTrue();
  }

  private InvitationEntity generateInvitationEntity() {
    return Instancio.of(InvitationEntity.class)
        .generate(field(InvitationEntity::getRole), gen -> gen.oneOf(BusinessUserRole.values()).asString())
        .generate(field(InvitationEntity::getStatus), gen -> gen.oneOf(InvitationStatus.values()).asString())
        .create();
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void deleteInvitation(Integer businessId, Integer invitationId) {
    // Given
    // When
    invitationRepositoryAdapter.deleteInvitation(businessId, invitationId);

    // Then
    // No exception thrown, method executed successfully
    verify(invitationJpaRepository).deleteByIdAndBusiness_Id(invitationId, businessId);
  }

}

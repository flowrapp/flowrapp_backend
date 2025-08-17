package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.InvitationJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.InvitationEntityMapper;
import io.github.flowrapp.model.Invitation;
import io.github.flowrapp.model.InvitationStatus;
import io.github.flowrapp.port.output.InvitationRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationRepositoryAdapter implements InvitationRepositoryOutput {

  private final InvitationJpaRepository invitationJpaRepository;

  private final InvitationEntityMapper invitationEntityMapper;

  @Override
  public Optional<Invitation> findById(Integer invitationId) {
    return invitationJpaRepository.findById(invitationId)
        .map(invitationEntityMapper::infra2domain);
  }

  @Override
  public Optional<Invitation> findByToken(@NonNull UUID token) {
    return invitationJpaRepository.findByToken(token)
        .map(invitationEntityMapper::infra2domain);
  }

  @Override
  public List<Invitation> findByBusinessIdAndStatus(@NonNull Integer businessId, @NonNull InvitationStatus status) {
    return invitationEntityMapper.infra2domain(
        invitationJpaRepository.findAllByBusiness_IdAndStatus(businessId, status.name()));
  }

  @Override
  public boolean userIsAlreadyInvitedToBusiness(Integer invitedUserId, Integer businessId) {
    return invitationJpaRepository.existsByInvited_IdAndBusiness_IdAndStatusIs(invitedUserId, businessId, InvitationStatus.PENDING.name());
  }

  @Override
  public @NonNull Invitation save(@NonNull Invitation invitation) {
    val jpaInvitation = invitationJpaRepository.save(
        invitationEntityMapper.domain2Infra(invitation));

    return invitationEntityMapper.infra2domain(jpaInvitation);
  }

  @Transactional
  @Override
  public void deleteInvitation(@NonNull Integer businessId, @NonNull Integer invitationId) {
    invitationJpaRepository.deleteByIdAndBusiness_Id(invitationId, businessId);
  }

}

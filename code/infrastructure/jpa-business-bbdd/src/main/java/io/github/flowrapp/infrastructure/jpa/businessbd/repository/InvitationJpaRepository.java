package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationJpaRepository extends JpaRepository<InvitationEntity, Integer> {

  Optional<InvitationEntity> findByToken(@NotNull UUID token);

  List<InvitationEntity> findAllByBusiness_IdAndStatus(Integer businessId, String status);

  List<InvitationEntity> findAllByInvited_IdAndStatus(Integer invitedUserId, String status);

  boolean existsByInvited_IdAndBusiness_IdAndStatusIs(Integer invitedUserId, Integer businessId, String status);

  void deleteByIdAndBusiness_Id(Integer id, Integer businessId);

}

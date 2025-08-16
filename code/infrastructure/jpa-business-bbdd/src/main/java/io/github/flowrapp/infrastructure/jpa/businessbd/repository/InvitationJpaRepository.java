package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.Optional;
import java.util.UUID;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationJpaRepository extends JpaRepository<InvitationEntity, Integer> {

  Optional<InvitationEntity> findByToken(@NotNull UUID token);

}

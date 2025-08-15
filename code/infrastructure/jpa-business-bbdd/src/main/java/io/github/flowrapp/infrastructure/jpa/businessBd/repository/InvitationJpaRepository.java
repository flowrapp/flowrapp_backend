package io.github.flowrapp.infrastructure.jpa.businessBd.repository;

import io.github.flowrapp.infrastructure.jpa.businessBd.entity.InvitationEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationJpaRepository extends JpaRepository<InvitationEntity, Integer> {
}

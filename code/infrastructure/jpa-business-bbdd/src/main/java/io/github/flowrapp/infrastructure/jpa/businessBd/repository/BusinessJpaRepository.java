package io.github.flowrapp.infrastructure.jpa.businessBd.repository;

import io.github.flowrapp.infrastructure.jpa.businessBd.entity.BusinessEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessJpaRepository extends JpaRepository<BusinessEntity, Integer> {
}

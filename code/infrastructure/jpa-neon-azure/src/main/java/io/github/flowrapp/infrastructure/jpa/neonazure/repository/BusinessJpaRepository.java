package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.BusinessEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessJpaRepository extends JpaRepository<BusinessEntity, Integer> {
}

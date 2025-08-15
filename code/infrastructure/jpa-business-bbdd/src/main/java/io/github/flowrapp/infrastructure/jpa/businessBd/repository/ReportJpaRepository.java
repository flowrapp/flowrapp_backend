package io.github.flowrapp.infrastructure.jpa.businessBd.repository;

import io.github.flowrapp.infrastructure.jpa.businessBd.entity.ReportEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, Integer> {
}

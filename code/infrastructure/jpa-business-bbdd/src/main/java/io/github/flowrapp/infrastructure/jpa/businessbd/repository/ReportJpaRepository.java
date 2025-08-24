package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, Integer>, JpaSpecificationExecutor<ReportEntity> {
}

package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportIdEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, ReportIdEntity>, QuerydslPredicateExecutor<ReportEntity> {
}

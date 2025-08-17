package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.WorklogEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorklogJpaRepository extends JpaRepository<WorklogEntity, Integer>, QuerydslJpaPredicateExecutor<WorklogEntity> {
}

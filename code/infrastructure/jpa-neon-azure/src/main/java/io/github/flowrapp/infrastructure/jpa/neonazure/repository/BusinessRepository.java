package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Integer> {
}
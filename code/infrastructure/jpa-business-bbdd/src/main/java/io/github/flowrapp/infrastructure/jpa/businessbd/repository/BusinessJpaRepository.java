package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessJpaRepository extends JpaRepository<BusinessEntity, Integer> {

  Optional<BusinessEntity> findByName(String name);

}

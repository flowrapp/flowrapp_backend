package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.MockUserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockUserJpaRepository extends JpaRepository<MockUserEntity, Long> {

  Optional<MockUserEntity> findByName(String name);

}

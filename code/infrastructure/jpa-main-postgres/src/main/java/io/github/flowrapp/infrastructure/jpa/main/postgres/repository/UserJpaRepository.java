package io.github.flowrapp.infrastructure.jpa.main.postgres.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.main.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByName(String name);

}

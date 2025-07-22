package io.github.flowrapp.infrastructure.jpa.main.postgres.repository;

import io.github.flowrapp.infrastructure.jpa.main.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}

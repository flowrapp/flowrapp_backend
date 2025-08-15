package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {

  Optional<UserEntity> findByMail(String mail);

}

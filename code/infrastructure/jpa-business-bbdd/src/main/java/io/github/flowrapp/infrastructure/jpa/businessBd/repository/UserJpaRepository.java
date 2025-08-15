package io.github.flowrapp.infrastructure.jpa.businessBd.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessBd.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {

  Optional<UserEntity> findByMail(String mail);

}

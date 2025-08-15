package io.github.flowrapp.infrastructure.jpa.businessBd.repository;

import io.github.flowrapp.infrastructure.jpa.businessBd.entity.UsersRoleEntity;
import io.github.flowrapp.infrastructure.jpa.businessBd.entity.UsersRoleIdEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRoleJpaRepository extends JpaRepository<UsersRoleEntity, UsersRoleIdEntity> {
}

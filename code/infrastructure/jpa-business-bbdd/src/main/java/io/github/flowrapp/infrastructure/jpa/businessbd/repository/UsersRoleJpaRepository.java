package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UsersRoleEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UsersRoleIdEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRoleJpaRepository extends JpaRepository<UsersRoleEntity, UsersRoleIdEntity> {
}

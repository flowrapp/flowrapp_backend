package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UsersRoleEntity;
import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UsersRoleId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRoleJpaRepository extends JpaRepository<UsersRoleEntity, UsersRoleId> {
}

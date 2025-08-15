package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UsersRoleEntity;
import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UsersRoleIdEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRoleJpaRepository extends JpaRepository<UsersRoleEntity, UsersRoleIdEntity> {
}

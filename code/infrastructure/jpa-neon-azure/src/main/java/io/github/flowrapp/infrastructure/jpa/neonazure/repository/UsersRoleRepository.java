package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UsersRole;
import io.github.flowrapp.infrastructure.jpa.neonazure.entity.UsersRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRoleRepository extends JpaRepository<UsersRole, UsersRoleId> {
}
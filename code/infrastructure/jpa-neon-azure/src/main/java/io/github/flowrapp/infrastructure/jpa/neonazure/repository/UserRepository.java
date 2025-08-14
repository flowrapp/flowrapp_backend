package io.github.flowrapp.infrastructure.jpa.neonazure.repository;

import io.github.flowrapp.infrastructure.jpa.neonazure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
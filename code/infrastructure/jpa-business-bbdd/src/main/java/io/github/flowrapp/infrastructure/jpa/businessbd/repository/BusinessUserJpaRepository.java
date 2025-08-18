package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessUserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UsersRoleIdEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUserJpaRepository extends JpaRepository<BusinessUserEntity, UsersRoleIdEntity> {

  boolean existsByUser_IdAndBusiness_Id(Integer userId, Integer businessId);

  Optional<BusinessUserEntity> findByUser_IdAndBusiness_Id(Integer userId, Integer businessId);

}

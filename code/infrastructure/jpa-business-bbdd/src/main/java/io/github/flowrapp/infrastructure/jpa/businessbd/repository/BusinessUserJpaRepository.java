package io.github.flowrapp.infrastructure.jpa.businessbd.repository;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessUserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UsersRoleIdEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUserJpaRepository extends JpaRepository<BusinessUserEntity, UsersRoleIdEntity>,
    QuerydslPredicateExecutor<BusinessUserEntity> {

  List<BusinessUserEntity> findByUser_Id(Integer userId);

  boolean existsByUser_IdAndBusiness_Id(Integer userId, Integer businessId);

  Optional<BusinessUserEntity> findByUser_IdAndBusiness_Id(Integer userId, Integer businessId);

}

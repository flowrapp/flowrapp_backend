package com.inditex.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import com.inditex.flowrapp.DatabaseData;
import com.inditex.flowrapp.config.InitDatabase;

import io.github.flowrapp.Application;
import io.github.flowrapp.infrastructure.jpa.main.postgres.config.PostgresDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.main.postgres.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.main.postgres.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class, PostgresDatasourceConfig.class})
@InitDatabase
class UserJpaRepositoryIT {

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Test
  void testFindUserByName() {
    // GIVEN

    // WHEN
    var user = userJpaRepository.findByName(DatabaseData.USER_USERNAME);

    // THEN
    assertThat(user)
        .isPresent()
        .get()
        .returns(DatabaseData.USER_DNI, UserEntity::getDni)
        .returns(DatabaseData.USER_USERNAME, UserEntity::getName);
  }

}

package io.github.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.jpa.businessbd.config.BusinessBdDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.UserJpaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class, BusinessBdDatasourceConfig.class})
@InitDatabase
class UserJpaRepositoryIT {

  @Autowired
  private UserJpaRepository userJpaRepositoryIT;

  @Test
  void testFindUserByMail() {
    // GIVEN

    // WHEN
    var user = userJpaRepositoryIT.findByMail(DatabaseData.USER_MAIL);

    // THEN
    assertThat(user)
        .isPresent()
        .get()
        .returns(DatabaseData.USER_MAIL, UserEntity::getMail)
        .returns(DatabaseData.USER_PHONE, UserEntity::getPhone)
        .returns(DatabaseData.USER_NAME, UserEntity::getName);
  }

  @Test
  void findAllUsers_returnsAllUsers() {
    // WHEN
    var users = userJpaRepositoryIT.findAll();

    // THEN
    assertThat(users)
        .isNotNull()
        .isNotEmpty();
  }

}

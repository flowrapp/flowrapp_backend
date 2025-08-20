package io.github.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.jpa.businessbd.config.BusinessBdDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.UserJpaRepository;
import io.github.flowrapp.config.Constants;

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
    var user = userJpaRepositoryIT.findByMail(DatabaseData.TEST_USER_MAIL);

    // THEN
    assertThat(user)
        .isPresent()
        .get()
        .returns(DatabaseData.TEST_USER_MAIL, UserEntity::getMail)
        .returns(DatabaseData.TEST_USER_PHONE, UserEntity::getPhone)
        .returns(DatabaseData.TEST_USER_NAME, UserEntity::getName);
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

  @Test
  void findAllUsersOwnedBusiness() {
    // GIVEN

    // WHEN
    var ownedBusinesses = userJpaRepositoryIT.findByMail(Constants.ADMIN_USER_MAIL)
        .orElseThrow()
        .getOwnedBusinesses();

    // THEN
    assertThat(ownedBusinesses)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  void findAllUsersBusinessMemberships() {
    // GIVEN

    // WHEN
    var businessMemberships = userJpaRepositoryIT.findByMail(Constants.ADMIN_USER_MAIL)
        .orElseThrow()
        .getBusinessMemberships();

    // THEN
    assertThat(businessMemberships)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1);
  }

}

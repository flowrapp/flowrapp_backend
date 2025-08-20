package io.github.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.flowrapp.Application;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.jpa.businessbd.config.BusinessBdDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessJpaRepository;

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
class BusinessJpaRepositoryIT {

  @Autowired
  private BusinessJpaRepository businessJpaRepositoryIT;

  @Test
  void findAll() {
    // GIVEN

    // WHEN
    var businesses = businessJpaRepositoryIT.findAll();

    assertThat(businesses)
        .isNotNull()
        .isNotEmpty();
  }

  @Test
  void finAllMembers() {
    // GIVEN

    // WHEN
    var members = businessJpaRepositoryIT.findAll().stream()
        .flatMap(b -> b.getMembers().stream())
        .toList();

    assertThat(members)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  void findAllInvitations() {
    // GIVEN

    // WHEN
    var invitations = businessJpaRepositoryIT.findAll().stream()
        .flatMap(b -> b.getInvitations().stream())
        .toList();

    assertThat(invitations)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2);
  }

}

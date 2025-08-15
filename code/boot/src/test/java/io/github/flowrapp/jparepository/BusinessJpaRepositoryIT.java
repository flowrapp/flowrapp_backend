package io.github.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.flowrapp.Application;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.jpa.businessBd.config.BusinessBdDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.businessBd.repository.BusinessJpaRepository;
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

}

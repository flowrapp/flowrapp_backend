package io.github.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.jpa.neonazure.config.NeonAzureDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.neonazure.entity.MockUserEntity;
import io.github.flowrapp.infrastructure.jpa.neonazure.repository.MockUserJpaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ContextConfiguration(classes = {Application.class, NeonAzureDatasourceConfig.class})
@InitDatabase
class MockUserJpaRepositoryIT {

  @Autowired
  private MockUserJpaRepository mockUserJpaRepository;

  @Test
  void testFindUserByName() {
    // GIVEN

    // WHEN
    var user = mockUserJpaRepository.findByName(DatabaseData.USER_USERNAME);

    // THEN
    assertThat(user)
        .isPresent()
        .get()
        .returns(DatabaseData.USER_DNI, MockUserEntity::getDni)
        .returns(DatabaseData.USER_USERNAME, MockUserEntity::getName);
  }

}

package io.github.flowrapp.jparepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import io.github.flowrapp.Application;
import io.github.flowrapp.DatabaseData;
import io.github.flowrapp.config.InitDatabase;
import io.github.flowrapp.infrastructure.jpa.businessbd.config.BusinessBdDatasourceConfig;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.InvitationEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.InvitationJpaRepository;

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
class InvitationJpaRepositoryIT {

  @Autowired
  private InvitationJpaRepository invitationJpaRepository;

  @Test
  void findAll() {
    // GIVEN

    // WHEN
    var invitations = invitationJpaRepository.findAll();

    // THEN
    assertThat(invitations)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  void findByToken() {
    // GIVEN
    var tokenUUID = UUID.fromString(DatabaseData.INVITATION_TOKEN);

    // WHEN
    var invitation = invitationJpaRepository.findByToken(tokenUUID);

    // THEN
    assertThat(invitation)
        .isPresent()
        .get()
        .returns(tokenUUID, InvitationEntity::getToken);
  }

}

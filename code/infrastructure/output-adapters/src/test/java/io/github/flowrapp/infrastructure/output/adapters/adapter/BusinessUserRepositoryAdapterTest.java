package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessUserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.UserEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessUserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessUserEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.BusinessUserRole;
import io.github.flowrapp.model.User;
import io.github.flowrapp.value.BusinessFilterRequest;

import com.querydsl.core.types.Predicate;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class BusinessUserRepositoryAdapterTest {

  @Mock
  private BusinessUserJpaRepository businessUserJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

  @Spy
  @InjectMocks
  private BusinessEntityMapper businessEntityMapper = spy(Mappers.getMapper(BusinessEntityMapper.class));

  @Spy
  @InjectMocks
  private BusinessUserEntityMapper businessUserEntityMapper = spy(Mappers.getMapper(BusinessUserEntityMapper.class));

  @InjectMocks
  private BusinessUserRepositoryAdapter businessUserRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getByUserAndBusinessId(Integer userId, Integer businessId) {
    // Given
    var businessUserEntity = generateBusinessUser();

    when(businessUserJpaRepository.findByUser_IdAndBusiness_Id(userId, businessId))
        .thenReturn(Optional.of(businessUserEntity));

    // When
    var result = businessUserRepositoryAdapter.getByUserAndBusinessId(userId, businessId);

    // Then
    assertThat(result)
        .isNotNull()
        .isPresent();
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getByUser(Integer userId) {
    // Given
    var businessUserEntity = generateBusinessUser();
    when(businessUserJpaRepository.findByUser_Id(userId))
        .thenReturn(List.of(businessUserEntity));

    // When
    var result = businessUserRepositoryAdapter.findByUser(userId);

    // Then
    assertThat(result)
        .isNotNull()
        .hasSize(1);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getByFilter(Integer userId, Integer businessId, BusinessUserRole role) {
    // Given
    var businessUserEntity = generateBusinessUser();

    when(businessUserJpaRepository.findAll((Predicate) any()))
        .thenReturn(List.of(businessUserEntity));

    // When
    var result = businessUserRepositoryAdapter.findByFilter(new BusinessFilterRequest(userId, businessId, role));

    // Then
    assertThat(result)
        .isNotNull()
        .hasSize(1);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void save(BusinessUser invitation) {
    // Given
    var businessUserEntity = generateBusinessUser();

    when(businessUserJpaRepository.save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .satisfies(entity -> {
          assertThat(entity.getUser()).isNotNull().returns(invitation.user().mail(), UserEntity::getMail);
          assertThat(entity.getBusiness()).isNotNull().returns(invitation.business().id(), BusinessEntity::getId);
          assertThat(entity.getInvitedBy()).isNotNull().returns(invitation.invitedBy().id(), UserEntity::getId);
        })
        .returns(invitation.role().toString(), BusinessUserEntity::getRole)
        .returns(invitation.joinedAt(), BusinessUserEntity::getJoinedAt))))
            .thenReturn(businessUserEntity);

    // When
    var result = businessUserRepositoryAdapter.save(invitation);

    // Then
    assertThat(result)
        .isNotNull()
        .satisfies(businessUser -> {
          assertThat(businessUser.user()).isNotNull().returns(businessUserEntity.getUser().getMail(), User::mail);
          assertThat(businessUser.business()).isNotNull().returns(businessUserEntity.getBusiness().getId(), Business::id);
          assertThat(businessUser.invitedBy()).isNotNull().returns(businessUserEntity.getInvitedBy().getId(), User::id);
        })
        .returns(businessUserEntity.getJoinedAt(), BusinessUser::joinedAt)
        .returns(businessUserEntity.getRole(), user -> user.role().toString());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void userIsMemberOfBusiness(Integer userId, Integer businessId) {
    // Given
    when(businessUserJpaRepository.existsByUser_IdAndBusiness_Id(userId, businessId))
        .thenReturn(true);

    // When
    boolean result = businessUserRepositoryAdapter.userIsMemberOfBusiness(userId, businessId);

    // Then
    assertThat(result).isTrue();
  }

  private BusinessUserEntity generateBusinessUser() {
    return Instancio.of(BusinessUserEntity.class)
        .generate(field(BusinessUserEntity::getRole), gen -> gen.oneOf(BusinessUserRole.values()).asString())
        .create();
  }

}

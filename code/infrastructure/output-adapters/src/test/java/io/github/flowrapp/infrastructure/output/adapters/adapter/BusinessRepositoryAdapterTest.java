package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.BusinessEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.BusinessJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.Business;
import io.github.flowrapp.model.User;

import lombok.val;
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
class BusinessRepositoryAdapterTest {

  @Mock
  private BusinessJpaRepository businessJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

  @Spy
  @InjectMocks
  private BusinessEntityMapper businessEntityMapper = spy(Mappers.getMapper(BusinessEntityMapper.class));

  @InjectMocks
  private BusinessRepositoryAdapter businessRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource
  void findById(Integer id, BusinessEntity businessEntity) {
    // GIVEN
    when(businessJpaRepository.findById(id))
        .thenReturn(Optional.of(businessEntity));

    // WHEN
    val result = businessRepositoryAdapter.findById(id);

    // THEN
    assertThat(result)
        .isPresent()
        .get()
        .returns(businessEntity.getName(), Business::name);
  }

  @ParameterizedTest
  @InstancioSource
  void findByName(String name, BusinessEntity businessEntity) {
    // GIVEN
    when(businessJpaRepository.findByName(name))
        .thenReturn(Optional.of(businessEntity));

    // WHEN
    val result = businessRepositoryAdapter.findByName(name);

    // THEN
    assertThat(result)
        .isPresent()
        .get()
        .returns(businessEntity.getName(), Business::name)
        .returns(businessEntity.getLongitude(), business -> business.location().longitude())
        .returns(businessEntity.getLatitude(), business -> business.location().latitude())
        .returns(businessEntity.getArea(), business -> business.location().area())
        .returns(businessEntity.getCreatedAt(), Business::createdAt)
        .extracting(Business::owner)
        .returns(businessEntity.getOwner().getId(), User::id)
        .returns(businessEntity.getOwner().getName(), User::name)
        .returns(businessEntity.getOwner().getMail(), User::mail)
        .returns(businessEntity.getOwner().getPhone(), User::phone)
        .returns(businessEntity.getOwner().getPasswordHash(), User::passwordHash)
        .returns(businessEntity.getOwner().getEnabled(), User::enabled)
        .returns(businessEntity.getOwner().getCreatedAt(), User::createdAt);
  }

  @ParameterizedTest
  @InstancioSource
  void save(Business business, BusinessEntity businessEntity) {
    // GIVEN
    when(businessJpaRepository.save(argThat(argument -> argument.getId().equals(business.id()))))
        .thenReturn(businessEntity);

    // WHEN
    val result = businessRepositoryAdapter.save(business);

    // THEN
    assertNotNull(result); // Verification is done on findByName method
    verify(businessJpaRepository).save(assertArg(argument -> assertThat(argument)
        .returns(business.name(), BusinessEntity::getName)
        .returns(business.location().longitude(), BusinessEntity::getLongitude)
        .returns(business.location().latitude(), BusinessEntity::getLatitude)
        .returns(business.location().area(), BusinessEntity::getArea)
        .returns(business.createdAt(), BusinessEntity::getCreatedAt)
        .returns(business.owner().id(), owner -> owner.getOwner().getId())
        .returns(business.owner().name(), owner -> owner.getOwner().getName())
        .returns(business.owner().mail(), owner -> owner.getOwner().getMail())
        .returns(business.owner().phone(), owner -> owner.getOwner().getPhone())
        .returns(business.owner().passwordHash(), owner -> owner.getOwner().getPasswordHash())
        .returns(business.owner().enabled(), owner -> owner.getOwner().getEnabled())
        .returns(business.owner().createdAt(), owner -> owner.getOwner().getCreatedAt())));
  }

}

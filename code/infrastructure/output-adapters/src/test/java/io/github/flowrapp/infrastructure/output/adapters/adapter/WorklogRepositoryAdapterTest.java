package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.WorklogEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.WorklogJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.WorklogEntityMapper;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.utils.DateUtils;
import io.github.flowrapp.value.WorklogFilteredRequest;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
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
class WorklogRepositoryAdapterTest {

  @Mock
  private WorklogJpaRepository worklogJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = spy(Mappers.getMapper(UserEntityMapper.class));

  @Spy
  @InjectMocks
  private BusinessEntityMapper businessEntityMapper = spy(Mappers.getMapper(BusinessEntityMapper.class));

  @Spy
  @InjectMocks
  private WorklogEntityMapper worklogEntityMapper = spy(Mappers.getMapper(WorklogEntityMapper.class));

  @InjectMocks
  private WorklogRepositoryAdapter worklogRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void findById(Integer worklogId, WorklogEntity worklogEntity) {
    // Given
    when(worklogJpaRepository.findById(worklogId))
        .thenReturn(Optional.of(worklogEntity));

    // When
    val result = worklogRepositoryAdapter.findById(worklogId);

    // Then
    assertThat(result)
        .isPresent()
        .get()
        .returns(worklogEntity.getId(), Worklog::id)
        .returns(DateUtils.toZone(worklogEntity.getBusiness().getTimezoneOffset()).apply(worklogEntity.getClockIn()), Worklog::clockIn)
        .returns(DateUtils.toZone(worklogEntity.getBusiness().getTimezoneOffset()).apply(worklogEntity.getClockOut()), Worklog::clockOut)
        .returns(worklogEntity.getCreatedAt(), Worklog::createdAt)
        .returns(worklogEntity.getUser().getId(), worklog -> worklog.user().id())
        .returns(worklogEntity.getBusiness().getId(), worklog -> worklog.business().id());
  }

  @ParameterizedTest
  @InstancioSource(samples = 1)
  void findAllFiltered(WorklogFilteredRequest worklogFilteredRequest, List<WorklogEntity> worklogEntities) {
    // Given
    when(worklogJpaRepository.findAll(any(Predicate.class), any(OrderSpecifier.class)))
        .thenReturn(worklogEntities);

    // When
    val result = worklogRepositoryAdapter.findAllFiltered(worklogFilteredRequest);

    // Then
    assertThat(result)
        .isNotNull()
        .isNotEmpty()
        .hasSize(worklogEntities.size());
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void doesOverlap(Worklog worklog, boolean doesOverlap) {
    // Given
    when(worklogJpaRepository.exists(any(Predicate.class)))
        .thenReturn(doesOverlap);

    // When
    val result = worklogRepositoryAdapter.doesOverlap(worklog);

    // Then
    assertThat(result)
        .isEqualTo(doesOverlap);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void save(Worklog worklogReq, WorklogEntity worklogEntity) {
    // Given
    when(worklogJpaRepository.save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(worklogReq.id(), WorklogEntity::getId)
        .returns(worklogReq.user().id(), wk -> wk.getUser().getId())
        .returns(worklogReq.business().id(), wk -> wk.getBusiness().getId())
        .returns(worklogReq.clockIn(), WorklogEntity::getClockIn)
        .returns(worklogReq.clockOut(), WorklogEntity::getClockOut)
        .returns(worklogReq.createdAt(), WorklogEntity::getCreatedAt))))
            .thenReturn(worklogEntity);

    // When
    val result = worklogRepositoryAdapter.save(worklogReq);

    // Then
    assertThat(result)
        .isNotNull();
  }

}

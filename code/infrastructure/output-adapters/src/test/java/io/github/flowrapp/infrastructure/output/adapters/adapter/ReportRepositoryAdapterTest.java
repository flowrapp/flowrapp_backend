package io.github.flowrapp.infrastructure.output.adapters.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.ReportJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.BusinessEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.ReportEntityMapper;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.Report;
import io.github.flowrapp.value.TimesheetFilterRequest;

import com.querydsl.core.types.Predicate;
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
class ReportRepositoryAdapterTest {

  @Mock
  private ReportJpaRepository reportJpaRepository;

  @Spy
  private UserEntityMapper userEntityMapper = Mappers.getMapper(UserEntityMapper.class);

  @Spy
  @InjectMocks
  private BusinessEntityMapper businessEntityMapper = spy(Mappers.getMapper(BusinessEntityMapper.class));

  @Spy
  @InjectMocks
  private ReportEntityMapper reportEntityMapper = spy(Mappers.getMapper(ReportEntityMapper.class));

  @InjectMocks
  private ReportRepositoryAdapter reportRepositoryAdapter;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void findAll(TimesheetFilterRequest filter, ReportEntity reportEntity) {
    // GIVEN
    when(reportJpaRepository.findAll((Predicate) any()))
        .thenReturn(List.of(reportEntity));

    // WHEN
    var result = reportRepositoryAdapter.findAll(filter);

    // THEN
    assertThat(result)
        .isNotNull()
        .hasSize(1);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getByDay(Integer userId, Integer businessId, LocalDate day, ReportEntity reportEntity) {
    // GIVEN
    when(reportJpaRepository.findOne((Predicate) any()))
        .thenReturn(Optional.of(reportEntity));

    // WHEN
    var result = reportRepositoryAdapter.getByDay(userId, businessId, day);

    // THEN
    assertThat(result)
        .isNotNull()
        .isPresent()
        .get()
        .returns(reportEntity.getUser().getId(), report -> report.user().id())
        .returns(reportEntity.getBusiness().getId(), report -> report.business().id())
        .returns(reportEntity.getId().getClockDay(), Report::day)
        .returns(BigDecimal.valueOf(reportEntity.getHours()), Report::hours);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void save(Report report, ReportEntity reportEntity) {
    // GIVEN
    when(reportJpaRepository.save(assertArg(argument -> assertThat(argument)
        .isNotNull()
        .returns(report.user().id(), r -> r.getUser().getId())
        .returns(report.business().id(), r -> r.getBusiness().getId())
        .returns(report.day(), reportEntity1 -> reportEntity1.getId().getClockDay())
        .returns(Objects.requireNonNull(report.hours()).doubleValue(), ReportEntity::getHours))))
            .thenReturn(reportEntity);

    // WHEN
    var result = reportRepositoryAdapter.save(report);

    // THEN
    assertThat(result)
        .isNotNull()
        .returns(reportEntity.getUser().getId(), r -> r.user().id());
  }

}

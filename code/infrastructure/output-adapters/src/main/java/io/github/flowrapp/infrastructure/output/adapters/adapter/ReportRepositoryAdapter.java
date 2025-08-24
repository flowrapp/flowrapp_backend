package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.ReportJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.ReportEntityMapper;
import io.github.flowrapp.model.Report;
import io.github.flowrapp.port.output.ReportRepositoryOutput;
import io.github.flowrapp.value.TimesheetFilterRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepositoryOutput {

  private final ReportJpaRepository reportJpaRepository;

  private final ReportEntityMapper reportEntityMapper;

  @Override
  public List<Report> findAll(@NonNull TimesheetFilterRequest filter) {
    return reportEntityMapper.infra2domain(
        reportJpaRepository.findAll(
            this.filter2specification(filter)));
  }

  @Override
  public Optional<Report> getByDay(@NonNull Integer userId, @NonNull Integer businessId, @NonNull LocalDate day) {
    var example = Example.of(
        reportEntityMapper.filter2example(userId, businessId, day));

    return reportJpaRepository.findOne(example)
        .map(reportEntityMapper::infra2domain);
  }

  @Override
  public Report save(Report report) {
    return reportEntityMapper.infra2domain(
        reportJpaRepository.save(
            reportEntityMapper.domain2Infra(report)));
  }

  private Specification<ReportEntity> filter2specification(TimesheetFilterRequest filter) {
    return (root, query, criteriaBuilder) -> {
      var predicates = criteriaBuilder.conjunction();

      if (filter.userId() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.equal(root.get("user").get("id"), filter.userId()));
      }

      if (filter.businessId() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.equal(root.get("business").get("id"), filter.businessId()));
      }

      if (filter.from() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.greaterThanOrEqualTo(root.get("id").get("clockDay"), filter.from()));
      }

      if (filter.to() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.lessThanOrEqualTo(root.get("id").get("clockDay"), filter.to()));
      }

      return predicates;
    };
  }

}

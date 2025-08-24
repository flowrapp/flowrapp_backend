package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.WorklogEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.WorklogJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.WorklogEntityMapper;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;
import io.github.flowrapp.value.WorklogFilteredRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorklogRepositoryAdapter implements WorklogRepositoryOutput {

  private final WorklogJpaRepository worklogJpaRepository;

  private final WorklogEntityMapper worklogEntityMapper;

  @Override
  public Optional<Worklog> findById(Integer worklogId) {
    return worklogJpaRepository.findById(worklogId)
        .map(worklogEntityMapper::infra2domain);
  }

  @Override
  public List<Worklog> findAllFiltered(WorklogFilteredRequest worklogFilteredRequest) {
    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.findAll(
            this.specFromWorkFilteredRequest(worklogFilteredRequest)));
  }

  @Override
  public boolean doesOverlap(Worklog worklog) {
    return worklogJpaRepository.exists(
        this.specFromWorklog(worklog));
  }

  @Override
  public Worklog save(Worklog worklog) {
    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.save(
            worklogEntityMapper.domain2Infra(worklog)));
  }

  private Specification<WorklogEntity> specFromWorklog(Worklog worklog) {
    return (root, query, criteriaBuilder) -> {
      var predicates = criteriaBuilder.conjunction();

      if (worklog.id() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.notEqual(root.get("id"), worklog.id())); // Exclude the current worklog if it has an ID
      }
      if (worklog.user() != null && worklog.user().id() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.equal(root.get("user").get("id"), worklog.user().id()));
      }
      if (worklog.business() != null && worklog.business().id() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.equal(root.get("business").get("id"), worklog.business().id()));
      }
      if (worklog.clockIn() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.greaterThanOrEqualTo(root.get("clockIn"), worklog.clockIn()));
      }
      if (worklog.clockOut() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.lessThanOrEqualTo(root.get("clockOut"), worklog.clockOut()));
      }

      predicates = criteriaBuilder.and(predicates,
          criteriaBuilder.isNotNull(root.get("clockOut"))); // Only match closed worklogs

      return predicates;
    };
  }

  private Specification<WorklogEntity> specFromWorkFilteredRequest(WorklogFilteredRequest worklogFilteredRequest) {
    return (root, query, criteriaBuilder) -> {
      var predicates = criteriaBuilder.conjunction();

      if (worklogFilteredRequest.businessId() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.equal(root.get("business").get("id"), worklogFilteredRequest.businessId()));
      }
      if (worklogFilteredRequest.userId() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.equal(root.get("user").get("id"), worklogFilteredRequest.userId()));
      }
      if (worklogFilteredRequest.from() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.greaterThanOrEqualTo(root.get("clockIn"), worklogFilteredRequest.from()));
      }
      if (worklogFilteredRequest.to() != null) {
        predicates = criteriaBuilder.and(predicates,
            criteriaBuilder.lessThanOrEqualTo(root.get("clockOut"), worklogFilteredRequest.to()));
      }

      return predicates;
    };
  }

}

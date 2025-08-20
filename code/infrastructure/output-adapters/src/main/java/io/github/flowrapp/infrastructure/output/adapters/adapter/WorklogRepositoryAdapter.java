package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.QWorklogEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.WorklogJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.WorklogEntityMapper;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;
import io.github.flowrapp.value.WorklogFilteredRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorklogRepositoryAdapter implements WorklogRepositoryOutput {

  private final WorklogJpaRepository worklogJpaRepository;

  private final WorklogEntityMapper worklogEntityMapper;

  private static final QWorklogEntity qWorklog = QWorklogEntity.worklogEntity;

  @Override
  public Optional<Worklog> findById(Integer worklogId) {
    return worklogJpaRepository.findById(worklogId)
        .map(worklogEntityMapper::infra2domain);
  }

  @Override
  public List<Worklog> findAllFiltered(WorklogFilteredRequest worklogFilteredRequest) {
    val predicate = qWorklog.isNotNull()
        .and(worklogFilteredRequest.businessId() != null ? qWorklog.business.id.eq(worklogFilteredRequest.businessId()) : null)
        .and(worklogFilteredRequest.userId() != null ? qWorklog.user.id.eq(worklogFilteredRequest.userId()) : null)
        .and(worklogFilteredRequest.from() != null ? qWorklog.clockIn.goe(worklogFilteredRequest.from()) : null)
        .and(worklogFilteredRequest.to() != null ? qWorklog.clockOut.loe(worklogFilteredRequest.to()) : null);

    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.findAll(predicate, QWorklogEntity.worklogEntity.clockIn.asc()));
  }

  @Override
  public boolean doesOverlap(Worklog worklog) {
    val predicate = qWorklog.isNotNull()
        .and(worklog.id() != null ? qWorklog.id.eq(worklog.id()).not() : null) // Exclude the current worklog if it has an ID
        .and(qWorklog.user.id.eq(worklog.user().id()))
        .and(qWorklog.business.id.eq(worklog.business().id()))
        .and(qWorklog.clockIn.goe(worklog.clockIn()))
        .and(qWorklog.clockIn.loe(worklog.clockOut()))
        .and(qWorklog.clockOut.isNotNull()); // Only match closed worklogs

    return worklogJpaRepository.exists(predicate);
  }

  @Override
  public Worklog save(Worklog worklog) {
    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.save(
            worklogEntityMapper.domain2Infra(worklog)));
  }

}

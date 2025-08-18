package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.QWorklogEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.WorklogJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.WorklogEntityMapper;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.model.value.WorklogFilteredRequest;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;

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

  @Override
  public Optional<Worklog> findById(Integer worklogId) {
    return worklogJpaRepository.findById(worklogId)
        .map(worklogEntityMapper::infra2domain);
  }

  @Override
  public List<Worklog> findAllFiltered(WorklogFilteredRequest worklogFilteredRequest) {
    val qWorklog = QWorklogEntity.worklogEntity;
    val predicate = qWorklog.isNotNull()
        .and(worklogFilteredRequest.businessId() != null ? qWorklog.business.id.eq(worklogFilteredRequest.businessId()) : null)
        .and(worklogFilteredRequest.userId() != null ? qWorklog.user.id.eq(worklogFilteredRequest.userId()) : null)
        .and(worklogFilteredRequest.from() != null ? qWorklog.clockIn.after(worklogFilteredRequest.from()) : null)
        .and(worklogFilteredRequest.to() != null ? qWorklog.clockOut.before(worklogFilteredRequest.to()) : null);

    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.findAll(predicate, QWorklogEntity.worklogEntity.clockIn.asc()));
  }

  @Override
  public Worklog save(Worklog worklog) {
    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.save(
            worklogEntityMapper.domain2Infra(worklog)));
  }

}

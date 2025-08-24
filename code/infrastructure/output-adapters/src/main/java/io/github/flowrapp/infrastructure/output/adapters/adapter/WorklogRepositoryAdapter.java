package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.WorklogJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.WorklogEntityMapper;
import io.github.flowrapp.model.Worklog;
import io.github.flowrapp.port.output.WorklogRepositoryOutput;
import io.github.flowrapp.value.WorklogFilteredRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        worklogJpaRepository.findAll());
  }

  @Override
  public boolean doesOverlap(Worklog worklog) {
    return true;
  }

  @Override
  public Worklog save(Worklog worklog) {
    return worklogEntityMapper.infra2domain(
        worklogJpaRepository.save(
            worklogEntityMapper.domain2Infra(worklog)));
  }

}

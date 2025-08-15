package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.neonazure.repository.MockUserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.AdapterMapper;
import io.github.flowrapp.model.MockUser;
import io.github.flowrapp.port.output.MockUserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MockUserRepositoryAdapter implements MockUserRepositoryOutput {

  private final AdapterMapper adapterMapper;

  private final MockUserJpaRepository mockUserJpaRepository;

  @Override
  public Optional<MockUser> findUserByName(@NonNull String user) {
    return mockUserJpaRepository.findByName(user)
        .map(adapterMapper::jpa2domain);
  }

}

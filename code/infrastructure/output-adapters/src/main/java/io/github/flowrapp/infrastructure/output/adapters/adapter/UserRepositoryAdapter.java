package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.neonazure.repository.UserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.AdapterMapper;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.UserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRepositoryAdapter implements UserRepositoryOutput {

  private final AdapterMapper adapterMapper;

  private final UserJpaRepository userJpaRepository;

  @Override
  public Optional<User> findUserByName(@NonNull String user) {
    return userJpaRepository.findByName(user)
        .map(adapterMapper::jpa2domain);
  }

}

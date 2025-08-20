package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.repository.UserJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.UserEntityMapper;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.UserRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryOutput {

  private final UserJpaRepository userJpaRepository;

  private final UserEntityMapper userEntityMapper;

  @Override
  public Optional<User> findUserByEmail(@NonNull String mail) {
    return userJpaRepository.findByMail(mail)
        .map(userEntityMapper::infra2domain);
  }

  @Override
  public boolean existsByEmail(@NonNull String email) {
    return userJpaRepository.existsByMail(email);
  }

  @Override
  public @NonNull User save(@NonNull User user) {
    val jpaUser = userJpaRepository.save(
        userEntityMapper.domain2Infra(user));

    return userEntityMapper.infra2domain(jpaUser);
  }

}

package io.github.flowrapp.infrastructure.output.adapters.adapter;

import io.github.flowrapp.infrastructure.output.adapters.mapper.AdapterMapper;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.UserRepositoryOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRepositoryAdapter implements UserRepositoryOutput {

    private final AdapterMapper adapterMapper;

    @Override
    public Optional<User> findUserByName(String user) {
        return Optional.empty(); // TODO
    }

}

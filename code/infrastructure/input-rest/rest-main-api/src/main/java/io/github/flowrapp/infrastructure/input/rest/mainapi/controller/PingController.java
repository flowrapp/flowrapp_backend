package io.github.flowrapp.infrastructure.input.rest.mainapi.controller;

import java.time.OffsetDateTime;

import io.github.flowrapp.infrastructure.apirest.users.api.SystemApi;
import io.github.flowrapp.infrastructure.apirest.users.model.Ping200ResponseDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PingController implements SystemApi {

  @Override
  public ResponseEntity<Ping200ResponseDTO> ping() {
    return ResponseEntity.ok(
        new Ping200ResponseDTO()
            .status("System is up and running")
            .timestamp(OffsetDateTime.now()));
  }
}

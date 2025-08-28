package io.github.flowrapp.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecondsTest {

  @Test
  void formatted() {
    final var threeHoursAndTwentyMinutes = Seconds.of(3 * 3600 + 20 * 60);
    assertEquals("3h 20m", threeHoursAndTwentyMinutes.formatted());
  }
}

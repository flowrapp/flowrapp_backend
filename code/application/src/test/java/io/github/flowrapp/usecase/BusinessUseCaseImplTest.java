package io.github.flowrapp.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import io.github.flowrapp.model.BusinessUser;
import io.github.flowrapp.model.User;
import io.github.flowrapp.port.output.BusinessUserRepositoryOutput;
import io.github.flowrapp.port.output.UserSecurityContextHolderOutput;
import io.github.flowrapp.value.BusinessFilterRequest;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class BusinessUseCaseImplTest {

  @Mock
  private BusinessUserRepositoryOutput businessUserRepositoryOutput;

  @Mock
  private UserSecurityContextHolderOutput userSecurityContextHolderOutput;

  @InjectMocks
  private BusinessUseCaseImpl businessUseCase;

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getUserBusiness(User user, BusinessUser businessUser) {
    // GIVEN
    when(userSecurityContextHolderOutput.getCurrentUser())
        .thenReturn(user);
    when(businessUserRepositoryOutput.findByUser(user.id()))
        .thenReturn(List.of(businessUser));

    // WHEN
    var result = businessUseCase.getUserBusiness();

    // THEN
    assertThat(result)
        .isNotNull()
        .hasSize(1)
        .containsExactly(businessUser);
  }

  @ParameterizedTest
  @InstancioSource(samples = 20)
  void getBusinessUsers(BusinessFilterRequest filter, BusinessUser businessUser) {
    // GIVEN
    when(businessUserRepositoryOutput.findByFilter(filter))
        .thenReturn(List.of(businessUser));

    // WHEN
    var result = businessUseCase.getBusinessUsers(filter);

    // THEN
    assertThat(result)
        .isNotNull()
        .hasSize(1)
        .containsExactly(businessUser);
  }

}

package io.github.flowrapp.infrastructure.input.rest.mainapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation (Swagger).
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Flowrapp API")
            .description("REST API for Flowrapp Backend")
            .version("v1.0.0")
            .contact(new Contact()
                .name("Flowrapp Team")
                .email("flowraapp@gmail.com")
                .url("https://bump.sh/flowrapp/doc/backend"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}

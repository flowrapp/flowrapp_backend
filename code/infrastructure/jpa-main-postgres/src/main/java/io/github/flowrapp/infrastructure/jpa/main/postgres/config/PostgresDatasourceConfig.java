package io.github.flowrapp.infrastructure.jpa.main.postgres.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PostgresDatasourceConfig {

  @ConfigurationProperties(prefix = "spring.datasource.main-postgres")
  @Bean("main-postgres")
  @Primary
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .build();
  }

}

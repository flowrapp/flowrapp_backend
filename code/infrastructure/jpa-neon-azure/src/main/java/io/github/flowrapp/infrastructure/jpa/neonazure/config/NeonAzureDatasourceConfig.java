package io.github.flowrapp.infrastructure.jpa.neonazure.config;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Neon Azure PostgreSQL datasource. This configuration sets up the datasource, entity manager factory, and
 * transaction manager.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "neonAzureEntityManagerFactory",
    transactionManagerRef = "neonAzureTransactionManager",
    basePackages = {"io.github.flowrapp.infrastructure.jpa.neonazure"})
public class NeonAzureDatasourceConfig {

  @ConfigurationProperties(prefix = "spring.datasource.neon-azure")
  @Bean("neonAzureDatasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .build();
  }

  @Bean("neonAzureEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("neonAzureDatasource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("io.github.flowrapp.infrastructure.jpa.neonazure.entity")
        .persistenceUnit("neonAzurePU")
        .build();
  }

  @Bean("neonAzureTransactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("neonAzureEntityManagerFactory") EntityManagerFactory neonAzureEntityManagerFactory) {
    return new JpaTransactionManager(neonAzureEntityManagerFactory);
  }

}

package io.github.flowrapp.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@DirtiesContext
@Sql(scripts = {"/compose/postgres/postgrest-cleanUp.sql", "/compose/postgres/init/postgres-init.sql"}, // Clean the schema
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Documented
@Inherited
@Retention(RUNTIME)
@Target(TYPE)
public @interface InitDatabase {

}

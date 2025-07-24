package com.inditex.flowrapp.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@DirtiesContext
@Sql(scripts = {"/compose/postgres/postgrest-cleanUp.sql", "/compose/postgres/init/postgres-init.sql"}, // Clean the schema
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//
@Retention(RUNTIME)
@Target(TYPE)
public @interface InitDatabase {
}

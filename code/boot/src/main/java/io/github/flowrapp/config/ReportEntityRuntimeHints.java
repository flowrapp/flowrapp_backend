package io.github.flowrapp.config;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.entity.ReportIdEntity;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(ReportEntityRuntimeHints.ReportEntityRuntimeHintsRegistrar.class)
public class ReportEntityRuntimeHints {

  static class ReportEntityRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      // Register ReportEntity for reflection with all members
      hints.reflection()
          .registerType(ReportEntity.class, builder -> builder
              .withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                          MemberCategory.INVOKE_DECLARED_METHODS,
                          MemberCategory.DECLARED_FIELDS));

      // Register ReportIdEntity for reflection with all members
      hints.reflection()
          .registerType(ReportIdEntity.class, builder -> builder
              .withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                          MemberCategory.INVOKE_DECLARED_METHODS,
                          MemberCategory.DECLARED_FIELDS));
    }
  }
}

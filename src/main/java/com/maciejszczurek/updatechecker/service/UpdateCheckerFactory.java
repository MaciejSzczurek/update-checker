package com.maciejszczurek.updatechecker.service;

import com.maciejszczurek.updatechecker.application.model.Application;
import com.maciejszczurek.updatechecker.application.model.ApplicationType;
import com.maciejszczurek.updatechecker.checker.NameSetter;
import com.maciejszczurek.updatechecker.checker.UpdateChecker;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
public class UpdateCheckerFactory {

  private final AnnotationConfigApplicationContext context;
  private final ResourcePatternResolver resourcePatternResolver;
  private final Map<ApplicationType, Class<?>> applicationCheckerClasses = new EnumMap<>(
    ApplicationType.class
  );

  public UpdateCheckerFactory(
    @NotNull final AnnotationConfigApplicationContext context,
    final ResourcePatternResolver resourcePatternResolver
  ) {
    this.context = context;
    this.resourcePatternResolver = resourcePatternResolver;
  }

  @NotNull
  public UpdateChecker getUpdateChecker(
    @NotNull final Application application
  ) {
    final UpdateChecker updateChecker = (UpdateChecker) context.getBean(
      applicationCheckerClasses.get(application.getApplicationType()),
      application.getSiteUrl(),
      application.getCurrentVersion()
    );

    if (updateChecker instanceof NameSetter namedUpdateChecker) {
      namedUpdateChecker.setName(application.getName());
    }

    return updateChecker;
  }

  @PostConstruct
  public void init() throws IOException {
    final var metadataReader = new CachingMetadataReaderFactory(
      resourcePatternResolver
    );

    for (final var resource : resourcePatternResolver.getResources(
      "classpath*:%s/*.class".formatted(
          UpdateChecker.class.getPackageName().replace(".", "/")
        )
    )) {
      if (resource.isReadable()) {
        final var resourceMetadataReader = metadataReader.getMetadataReader(
          resource
        );
        final var updateCheckerClass = ClassUtils.resolveClassName(
          resourceMetadataReader.getClassMetadata().getClassName(),
          context.getClassLoader()
        );
        if (
          updateCheckerClass.getSuperclass() != null &&
          updateCheckerClass.getSuperclass().equals(UpdateChecker.class) &&
          resourceMetadataReader
            .getAnnotationMetadata()
            .isAnnotated(
              com.maciejszczurek.updatechecker.checker.annotation.ApplicationType.class.getName()
            )
        ) {
          if (updateCheckerClass.getConstructors().length == 0) {
            throw new UpdateCheckerFactoryException(
              "There is no public constructor for %s.".formatted(
                  updateCheckerClass.getSimpleName()
                )
            );
          }

          context.registerBean(
            updateCheckerClass,
            beanDefinition -> {
              beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
              ((AnnotatedGenericBeanDefinition) beanDefinition).setAutowireMode(
                  AbstractBeanDefinition.AUTOWIRE_BY_TYPE
                );
            }
          );
          applicationCheckerClasses.put(
            Objects
              .requireNonNull(
                AnnotationUtils.findAnnotation(
                  updateCheckerClass,
                  com.maciejszczurek.updatechecker.checker.annotation.ApplicationType.class
                )
              )
              .value(),
            updateCheckerClass
          );
        }
      }
    }

    if (applicationCheckerClasses.size() != ApplicationType.values().length) {
      throw new UpdateCheckerFactoryException(
        "Missing updater checkers: %s.".formatted(
            Arrays
              .stream(ApplicationType.values())
              .filter(applicationType ->
                !applicationCheckerClasses.containsKey(applicationType)
              )
              .map(Enum::toString)
              .collect(Collectors.joining(", "))
          )
      );
    }
  }
}

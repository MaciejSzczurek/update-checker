package com.maciejszczurek.updatechecker.application.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

@Entity
@Table(name = "applications")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
public class Application {

  @Id
  @GeneratedValue(generator = "application_seq")
  @SequenceGenerator(name = "application_seq", allocationSize = 1)
  private Long id;

  @Column(unique = true)
  @NotEmpty
  private String name;

  @Enumerated(EnumType.STRING)
  @NotNull
  private ApplicationType applicationType;

  @NotNull
  @NotEmpty
  private String siteUrl;

  private String updateUrl;

  @NotNull
  @NotEmpty
  private String currentVersion;

  private LocalDateTime lastUpdate;

  private boolean ignored;

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (
      object == null || Hibernate.getClass(this) != Hibernate.getClass(object)
    ) {
      return false;
    }

    final var application = (Application) object;

    return id != null && Objects.equals(id, application.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

package com.maciejszczurek.updatechecker.option.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

@Entity
@Table(name = "options")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class Option {

  @Id
  private String name;

  @Lob
  @NotNull
  @Column(name = "`value`")
  private String value;

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

    Option option = (Option) object;
    return name != null && Objects.equals(name, option.name);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

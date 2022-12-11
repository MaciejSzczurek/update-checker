package com.maciejszczurek.updatechecker.cookie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

@Entity
@Table(
  name = "cookies",
  indexes = { @Index(name = "uri_index", columnList = "uri") }
)
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public final class Cookie {

  @EmbeddedId
  private Id id;

  @Column(length = 1024, nullable = false, name = "`value`")
  private String value;

  private ZonedDateTime validUntil;
  private boolean secure;
  private boolean httpOnly;
  private Integer version;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var cookie = (Cookie) o;
    return id != null && Objects.equals(id, cookie.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Embeddable
  @NoArgsConstructor
  @Setter
  @Getter
  @Accessors(chain = true)
  public static final class Id implements Serializable {

    @Serial
    private static final long serialVersionUID = -766270389354952848L;

    @Column(length = 1024)
    private URL uri;

    @Column(length = 1024)
    private String name;

    @Column(length = 1024)
    private String domain;

    @Column(length = 1024)
    private String path;

    @Override
    public boolean equals(final Object object) {
      if (this == object) {
        return true;
      }

      if (
        object == null || Hibernate.getClass(this) != Hibernate.getClass(object)
      ) {
        return false;
      }

      final var id = (Id) object;

      return (
        uri != null &&
        Objects.equals(uri, id.uri) &&
        name != null &&
        Objects.equals(name, id.name) &&
        domain != null &&
        Objects.equals(domain, id.domain) &&
        path != null &&
        Objects.equals(path, id.path)
      );
    }

    @Override
    public int hashCode() {
      return Objects.hash(uri, name, domain, path);
    }
  }
}

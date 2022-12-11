package com.maciejszczurek.updatechecker.cookie.model;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CookieRepository extends JpaRepository<Cookie, Cookie.Id> {
  @SuppressWarnings("java:S100")
  List<Cookie> findAllById_Uri(URL uri);

  @Query("select cookie.id.uri from Cookie cookie")
  List<URI> findAllUris();

  void deleteAllByValidUntilBefore(ZonedDateTime validUntil);
}

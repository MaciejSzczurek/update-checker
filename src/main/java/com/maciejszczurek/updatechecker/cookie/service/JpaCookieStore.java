package com.maciejszczurek.updatechecker.cookie.service;

import com.maciejszczurek.updatechecker.cookie.model.Cookie;
import com.maciejszczurek.updatechecker.cookie.model.CookieRepository;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaCookieStore implements CookieStore {

  private final CookieRepository repository;

  @NotNull
  private static Cookie toCookie(
    final URL url,
    @NotNull final HttpCookie httpCookie
  ) {
    return new Cookie()
      .setId(
        new Cookie.Id()
          .setUri(url)
          .setName(httpCookie.getName())
          .setDomain(httpCookie.getDomain())
          .setPath(httpCookie.getPath())
      )
      .setValue(httpCookie.getValue())
      .setValidUntil(
        httpCookie.getMaxAge() != -1
          ? ZonedDateTime.now().plusSeconds(httpCookie.getMaxAge())
          : null
      )
      .setSecure(httpCookie.getSecure())
      .setHttpOnly(httpCookie.isHttpOnly())
      .setVersion(httpCookie.getVersion());
  }

  @NotNull
  private static HttpCookie toHttpCookie(@NotNull final Cookie cookie) {
    final var httpCookie = new HttpCookie(
      cookie.getId().getName(),
      cookie.getValue()
    );
    httpCookie.setDomain(cookie.getId().getDomain());
    httpCookie.setPath(cookie.getId().getPath());

    if (cookie.getValidUntil() != null) {
      httpCookie.setMaxAge(
        ZonedDateTime.now().until(cookie.getValidUntil(), ChronoUnit.SECONDS)
      );
    }
    httpCookie.setSecure(cookie.isSecure());
    httpCookie.setHttpOnly(cookie.isHttpOnly());

    if (cookie.getVersion() != null) {
      httpCookie.setVersion(cookie.getVersion());
    }

    return httpCookie;
  }

  private static boolean isExpired(@NotNull final Cookie cookie) {
    return Optional
      .ofNullable(cookie.getValidUntil())
      .map(validUntil -> validUntil.isBefore(ZonedDateTime.now()))
      .orElse(false);
  }

  @NotNull
  private static URL getEffectiveURI(@NotNull final URI uri)
    throws MalformedURLException {
    URI effectiveURI;
    try {
      effectiveURI = new URI("https", uri.getHost(), null, null, null);
    } catch (URISyntaxException ignored) {
      effectiveURI = uri;
    }

    return effectiveURI.toURL();
  }

  @SneakyThrows
  @Override
  public void add(final URI uri, final HttpCookie httpCookie) {
    final var cookie = toCookie(getEffectiveURI(uri), httpCookie);

    repository.delete(cookie);

    if (httpCookie.getMaxAge() != 0) {
      repository.save(cookie);
    }
  }

  @SneakyThrows
  @Override
  public List<HttpCookie> get(final URI uri) {
    final var cookies = repository.findAllById_Uri(getEffectiveURI(uri));

    if (cookies.isEmpty()) {
      return Collections.emptyList();
    }

    final var notExpiredCookies = cookies
      .parallelStream()
      .collect(Collectors.partitioningBy(cookie -> !isExpired(cookie)));

    repository.deleteAll(notExpiredCookies.get(Boolean.FALSE));

    return notExpiredCookies
      .get(Boolean.TRUE)
      .parallelStream()
      .map(JpaCookieStore::toHttpCookie)
      .toList();
  }

  @Override
  public List<HttpCookie> getCookies() {
    removeOldCookies();

    return repository
      .findAll()
      .stream()
      .map(JpaCookieStore::toHttpCookie)
      .toList();
  }

  @Override
  public List<URI> getURIs() {
    return Collections.unmodifiableList(repository.findAllUris());
  }

  @SneakyThrows
  @Override
  public boolean remove(final URI uri, final HttpCookie httpCookie) {
    final var cookie = repository.findById(
      toCookie(getEffectiveURI(uri), httpCookie).getId()
    );

    if (cookie.isEmpty()) {
      return false;
    }

    repository.delete(cookie.get());

    return true;
  }

  @Override
  public boolean removeAll() {
    if (repository.count() == 0) {
      return false;
    }

    repository.deleteAll();

    return true;
  }

  @EventListener(ContextRefreshedEvent.class)
  public void removeOldCookies() {
    repository.deleteAllByValidUntilBefore(ZonedDateTime.now());
  }
}

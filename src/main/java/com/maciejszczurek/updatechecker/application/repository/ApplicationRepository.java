package com.maciejszczurek.updatechecker.application.repository;

import com.maciejszczurek.updatechecker.application.model.Application;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository
  extends JpaRepository<Application, Long> {
  Optional<Application> findByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);

  List<Application> findByIgnoredIsFalse();
}

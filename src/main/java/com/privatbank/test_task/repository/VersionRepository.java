package com.privatbank.test_task.repository;

import com.privatbank.test_task.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    List<Version> findAllByCreatedAtBefore(LocalDateTime dateTime);

    Optional<Version> findById(Long id);

    @Query("SELECT v FROM Version v ORDER BY v.createdAt DESC LIMIT 1")
    Optional<Version> findNewestVersion();
}

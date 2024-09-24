package com.privatbank.test_task.repository;

import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRangeRepository extends JpaRepository<CardRange, Long> {
    List<CardRange> findAllByVersion(Version version);
    @Query("SELECT c FROM CardRange c WHERE c.version.id = :versionId AND :cardNumber BETWEEN c.minRange AND c.maxRange")
    Optional<CardRange> findByCardNumberInRangeAndVersion(@Param("cardNumber") BigInteger cardNumber, @Param("versionId") Long versionId);

}

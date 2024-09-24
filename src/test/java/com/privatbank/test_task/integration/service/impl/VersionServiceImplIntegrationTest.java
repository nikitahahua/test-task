package com.privatbank.test_task.integration.service.impl;

import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.repository.VersionRepository;
import com.privatbank.test_task.service.impl.CardRangeServiceImpl;
import com.privatbank.test_task.service.impl.VersionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class VersionServiceImplIntegrationTest {

    @Autowired
    private VersionServiceImpl versionService;
    @Autowired
    private CardRangeServiceImpl cardRangeService;

    @Autowired
    private VersionRepository versionRepository;

    @BeforeEach
    public void setUp() {
        versionRepository.deleteAll();
    }
    @Test
    public void testAddVersion() {
        Version version = new Version();
        version.setCreatedAt(LocalDateTime.now());

        Version savedVersion = versionService.addVersion(version);
        assertNotNull(savedVersion.getId());
        assertEquals(version.getCreatedAt(), savedVersion.getCreatedAt());
    }

    @Test
    public void testGetVersionsCreatedBefore() {
        LocalDateTime now = LocalDateTime.now();
        Version version1 = new Version();
        version1.setCreatedAt(now.minusDays(1));
        versionService.addVersion(version1);

        Version version2 = new Version();
        version2.setCreatedAt(now.minusDays(2));
        versionService.addVersion(version2);

        List<Version> versions = versionService.getVersionsCreatedBefore(now);
        assertEquals(2, versions.size());
    }

    @Test
    public void testUpdateVersionWithCardRange() {
        Version version = new Version();
        version.setCreatedAt(LocalDateTime.now());

        CardRange cardRange1 = new CardRange();
        cardRange1.setBin(123456);
        cardRange1.setMinRange(BigInteger.valueOf(1000));
        cardRange1.setMaxRange(BigInteger.valueOf(2000));

        version.setId(1L);
        List<CardRange> savedRanges = cardRangeService.saveAllCardRanges(List.of(cardRange1));
        version.setCardRanges(savedRanges);
        Version savedVersion = versionService.addVersion(version);

        CardRange cardRange2 = new CardRange();
        cardRange2.setBin(654321);
        cardRange2.setMinRange(BigInteger.valueOf(3000));
        cardRange2.setMaxRange(BigInteger.valueOf(4000));

        List<CardRange> updatedCardRanges = cardRangeService.saveAllCardRanges(List.of(savedRanges.get(0), cardRange2));
        savedVersion.setCardRanges(updatedCardRanges);
        savedVersion.setCreatedAt(LocalDateTime.now().plusDays(1));

        Version updatedVersion = versionService.updateVersion(savedVersion.getId(), savedVersion);

        assertEquals(2, updatedVersion.getCardRanges().size());
        assertTrue(updatedVersion.getCardRanges().contains(cardRange1));
        assertTrue(updatedVersion.getCardRanges().contains(cardRange2));
        assertEquals(savedVersion.getCreatedAt(), updatedVersion.getCreatedAt());
    }



    @Test
    public void testDeleteVersion() {
        Version version = new Version();
        version.setCreatedAt(LocalDateTime.now());
        Version savedVersion = versionService.addVersion(version);

        versionService.deleteVersion(savedVersion.getId());
        assertThrows(IllegalArgumentException.class, () -> versionService.updateVersion(savedVersion.getId(), savedVersion));
    }

    @Test
    public void testGetNewestVersion() {
        Version version1 = new Version();
        version1.setCreatedAt(LocalDateTime.now().minusDays(1));
        versionService.addVersion(version1);

        Version version2 = new Version();
        version2.setCreatedAt(LocalDateTime.now());
        versionService.addVersion(version2);

        Version newestVersion = versionService.getNewestVersion();
        assertEquals(version2.getCreatedAt(), newestVersion.getCreatedAt());
    }
}
package com.privatbank.test_task.integration.service.impl;

import com.privatbank.test_task.exceptions.CardRangeNotFoundException;
import com.privatbank.test_task.exceptions.WrongCardNumberException;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.repository.CardRangeRepository;
import com.privatbank.test_task.service.CardRangeService;
import com.privatbank.test_task.service.VersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("test")
public class CardRangeServiceImplIntegrationTest {

    @Autowired
    private CardRangeService cardRangeService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private CardRangeRepository cardRangeRepository;

    @BeforeEach
    public void setUp() {
        cardRangeRepository.deleteAll();
    }

    @Test
    public void testGetCardInRange_Success() {
        Version version = new Version();
        version.setCreatedAt(LocalDateTime.now());

        CardRange cardRange = new CardRange();
        cardRange.setBin(123456);
        cardRange.setMinRange(new BigInteger("5234560000000000000"));
        cardRange.setMaxRange(new BigInteger("5234569999999999999"));
        cardRange.setVersion(version);

        versionService.addVersion(version);
        List<CardRange> savedCardRanges = cardRangeService.saveAllCardRanges(List.of(cardRange));
        version.setCardRanges(savedCardRanges);
        versionService.updateVersion(version.getId(), version);

        BigInteger cardNumber = new BigInteger("5234560000000000");
        CardRange result = cardRangeService.getCardInRange(cardNumber);

        assertNotNull(result);
        assertEquals(cardRange.getBin(), result.getBin());
    }

    @Test
    public void testGetCardInRange_NotFound() {
        Version version = versionService.addVersion(new Version(LocalDateTime.now()));

        BigInteger invalidCardNumber = new BigInteger("9999990000000000");

        assertThrows(CardRangeNotFoundException.class, () -> {
            cardRangeService.getCardInRange(invalidCardNumber);
        });
    }

    @Test
    public void testGetCardInRange_InvalidCardNumber() {
        BigInteger invalidCardNumber = new BigInteger("123");

        assertThrows(WrongCardNumberException.class, () -> {
            cardRangeService.getCardInRange(invalidCardNumber);
        });
    }
}
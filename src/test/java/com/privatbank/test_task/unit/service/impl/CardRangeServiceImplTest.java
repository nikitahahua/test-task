package com.privatbank.test_task.unit.service.impl;

import com.privatbank.test_task.exceptions.CardRangeNotFoundException;
import com.privatbank.test_task.exceptions.WrongCardNumberException;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.repository.CardRangeRepository;
import com.privatbank.test_task.service.impl.CardRangeServiceImpl;
import com.privatbank.test_task.service.impl.VersionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardRangeServiceImplTest {

    @Mock
    private CardRangeRepository cardRangeRepository;

    @Mock
    private VersionServiceImpl versionService;

    @InjectMocks
    private CardRangeServiceImpl cardRangeService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCardInRangeWhenValidCardNumberThenReturnCardRange() {
        BigInteger cardNumber = new BigInteger("1234567812345678");
        Version version = new Version();
        version.setId(1L);

        when(versionService.getNewestVersion()).thenReturn(version);
        CardRange cardRange = new CardRange();
        cardRange.setBankName("Test Bank");
        cardRange.setAlphaCode("TEST");

        when(cardRangeRepository.findByCardNumberInRangeAndVersion(any(), any())).thenReturn(Optional.of(cardRange));

        CardRange result = cardRangeService.getCardInRange(cardNumber);

        assertNotNull(result);
        assertEquals("Test Bank", result.getBankName());
        assertEquals("TEST", result.getAlphaCode());
    }

    @Test
    public void testGetCardInRangeWhenInvalidCardNumberThenThrowWrongCardNumberException() {
        BigInteger invalidCardNumber = new BigInteger("123");
        Exception exception = assertThrows(WrongCardNumberException.class, () -> {
            cardRangeService.getCardInRange(invalidCardNumber);
        });

        assertEquals("Card number must be exactly 16 digits long.", exception.getMessage());
    }

    @Test
    public void testGetCardInRangeWhenNotFoundThenThrowCardRangeNotFoundException() {
        BigInteger cardNumber = new BigInteger("1234567812345678");
        Version version = new Version();
        version.setId(1L);

        when(versionService.getNewestVersion()).thenReturn(version);
        when(cardRangeRepository.findByCardNumberInRangeAndVersion(any(), any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(CardRangeNotFoundException.class, () -> {
            cardRangeService.getCardInRange(cardNumber);
        });

        assertEquals("Card number is not in range for the given version.", exception.getMessage());
    }

    @Test
    public void testGetCardRangesByVersionWhenValidVersionThenReturnCardRanges() {
        Version version = new Version();
        version.setId(1L);
        when(cardRangeRepository.findAllByVersion(version)).thenReturn(Collections.singletonList(new CardRange()));

        List<CardRange> result = cardRangeService.getCardRangesByVersion(version);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetCardRangesByVersionWhenNullVersionThenThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardRangeService.getCardRangesByVersion(null);
        });

        assertEquals("Version cannot be null", exception.getMessage());
    }

    @Test
    public void testSaveAllCardRangesWhenValidInputThenReturnSavedCardRanges() {
        CardRange cardRange = new CardRange();
        cardRange.setMinRange(BigInteger.valueOf(1000));
        cardRange.setMaxRange(BigInteger.valueOf(2000));
        cardRange.setBin(123);

        Version version = new Version(LocalDateTime.now());
        when(versionService.addVersion(any())).thenReturn(version);
        when(cardRangeRepository.saveAll(any())).thenReturn(Collections.singletonList(cardRange));

        List<CardRange> result = cardRangeService.saveAllCardRanges(Collections.singletonList(cardRange));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testSaveAllCardRangesWhenNullInputThenThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardRangeService.saveAllCardRanges(null);
        });

        assertEquals("The card ranges list cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testSaveAllCardRangesWhenEmptyInputThenThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardRangeService.saveAllCardRanges(Collections.emptyList());
        });

        assertEquals("The card ranges list cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testSaveAllCardRangesWhenInvalidCardRangeThenThrowIllegalArgumentException() {
        CardRange invalidCardRange = new CardRange();
        invalidCardRange.setMinRange(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cardRangeService.saveAllCardRanges(Collections.singletonList(invalidCardRange));
        });

        assertEquals("Some card ranges have invalid or null values", exception.getMessage());
    }
}
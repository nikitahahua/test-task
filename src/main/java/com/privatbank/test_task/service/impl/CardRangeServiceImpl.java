package com.privatbank.test_task.service.impl;

import com.privatbank.test_task.exceptions.CardRangeNotFoundException;
import com.privatbank.test_task.exceptions.WrongCardNumberException;
import com.privatbank.test_task.exceptions.WrongVersionException;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;
import com.privatbank.test_task.repository.CardRangeRepository;
import com.privatbank.test_task.service.CardRangeService;
import com.privatbank.test_task.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardRangeServiceImpl implements CardRangeService {

    private final CardRangeRepository cardRangeRepository;
    private final VersionService versionService;

    @Override
    public CardRange getCardInRange(BigInteger cardNumber) {
        if (cardNumber == null || cardNumber.toString().length() != 16) {
            log.warn("Invalid card number provided: {}", cardNumber);
            throw new WrongCardNumberException("Card number must be exactly 16 digits long.");
        }

        BigInteger rightNumber = cardNumber.multiply(BigInteger.valueOf(1000));
        Version newestVersion = versionService.getNewestVersion();
        Optional<CardRange> cardInfoOptional = cardRangeRepository.findByCardNumberInRangeAndVersion(rightNumber, newestVersion.getId());
        if (cardInfoOptional.isEmpty()) {
            log.warn("Card number {} is not in any range for version {}", cardNumber, newestVersion.getId());
            throw new CardRangeNotFoundException("Card number is not in range for the given version.");
        }

        return cardInfoOptional.get();
    }

    @Override
    public List<CardRange> getCardRangesByVersion(Version version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }

        List<CardRange> cardRanges = cardRangeRepository.findAllByVersion(version);
        if (cardRanges.isEmpty()) {
            throw new WrongVersionException("Invalid version provided");
        }

        return cardRanges;
    }

    @Transactional
    @Override
    public List<CardRange> saveAllCardRanges(List<CardRange> cardRangesList) {
        if (cardRangesList == null || cardRangesList.isEmpty()) {
            throw new IllegalArgumentException("The card ranges list cannot be null or empty.");
        }

        if (cardRangesList.stream().anyMatch(cardRange ->
                (cardRange == null) || (cardRange.getMinRange() == null)
                        || (cardRange.getMaxRange() == null) || (cardRange.getBin() == 0))) {
            throw new IllegalArgumentException("Some card ranges have invalid or null values");
        }

        Version addedVersion = versionService.addVersion(new Version(LocalDateTime.now()));
        cardRangesList.forEach(cardRange -> cardRange.setVersion(addedVersion));
        List<CardRange> savedCardRanges = cardRangeRepository.saveAll(cardRangesList);
        addedVersion.setCardRanges(savedCardRanges);
        versionService.updateVersion(addedVersion.getId(), addedVersion);
        log.info("Successfully saved card ranges");
        return cardRangeRepository.saveAll(cardRangesList);
    }

}

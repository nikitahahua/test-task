package com.privatbank.test_task.service;

import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.model.Version;

import java.math.BigInteger;
import java.util.List;

public interface CardRangeService {
    CardRange getCardInRange(BigInteger cardNumber);
    List<CardRange> getCardRangesByVersion(Version version);
    List<CardRange> saveAllCardRanges(List<CardRange> cardRangesList);
}

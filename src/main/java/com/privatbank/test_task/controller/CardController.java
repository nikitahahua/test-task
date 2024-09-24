package com.privatbank.test_task.controller;

import com.privatbank.test_task.model.CardInfoResponse;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.service.CardRangeService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;

@Slf4j
@Timed(value = "api.requests.total", description = "Total number of requests")
@RestController
@RequestMapping("/api")
public class CardController {

    private final Counter failedRequestsCounter;
    private final CardRangeService cardRangeService;

    public CardController(MeterRegistry meterRegistry, CardRangeService cardRangeService) {
        this.failedRequestsCounter = meterRegistry.counter("api.failed.requests");
        this.cardRangeService = cardRangeService;
    }

    @Timed(value = "api.requests.process.time", description = "Time taken to process requests")
    @PostMapping("/card-range/{cardNumber}")
    public ResponseEntity<CardInfoResponse> getInfoByCard(@PathVariable Long cardNumber) {
        try {
            CardRange cardRange = cardRangeService.getCardInRange(BigInteger.valueOf(cardNumber));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CardInfoResponse(cardRange.getBin(), cardRange.getAlphaCode(), cardRange.getBankName()));
        } catch (Exception e) {
            failedRequestsCounter.increment();
            log.error("Failed to get card info for card number {}: {}", cardNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}


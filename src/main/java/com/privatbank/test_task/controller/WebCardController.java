package com.privatbank.test_task.controller;

import com.privatbank.test_task.exceptions.CardRangeNotFoundException;
import com.privatbank.test_task.exceptions.WrongCardNumberException;
import com.privatbank.test_task.model.CardInfoResponse;
import com.privatbank.test_task.model.CardRange;
import com.privatbank.test_task.service.CardRangeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;

@Controller
public class WebCardController {

    private final CardRangeService cardRangeService;

    public WebCardController(CardRangeService cardRangeService) {
        this.cardRangeService = cardRangeService;
    }

    @GetMapping("/lookup")
    public String lookupCard(@RequestParam(value = "cardNumber", required = false) String cardNumber, Model model) {
        if (cardNumber != null && !cardNumber.isEmpty()) {
            try {
                CardRange cardRange = cardRangeService.getCardInRange(new BigInteger(cardNumber));
                model.addAttribute("cardInfo", new CardInfoResponse(cardRange.getBin(), cardRange.getAlphaCode(), cardRange.getBankName()));
            } catch (WrongCardNumberException e) {
                model.addAttribute("errorMessage", "Card number is invalid.");
            } catch (CardRangeNotFoundException e) {
                model.addAttribute("errorMessage", "Card number not found.");
            } catch (NumberFormatException e) {
                model.addAttribute("errorMessage", "Please enter a valid card number.");
            }
        } else {
            model.addAttribute("errorMessage", "Card number cannot be empty.");
        }
        return "get-info";
    }
}

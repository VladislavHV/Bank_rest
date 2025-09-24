package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardRequest;
import com.example.bankcards.dto.BankCardResponse;
import com.example.bankcards.service.BankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class BankCardController {

    @Autowired
    private BankCardService bankCardService;

    @PostMapping
    public ResponseEntity<BankCardResponse> createCard(
            @RequestBody BankCardRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        BankCardResponse response = bankCardService.createCard(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BankCardResponse>> getCards(Authentication authentication) {
        String username = authentication.getName();
        List<BankCardResponse> cards = bankCardService.getCards(username);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{cardId}/toggle")
    public ResponseEntity<Void> toggleCardStatus(@PathVariable Long cardId, Principal principal) {
        bankCardService.toggleCardStatus(cardId, principal.getName());
        return ResponseEntity.ok().build();
    }

}

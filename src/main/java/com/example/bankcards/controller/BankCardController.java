package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardRequest;
import com.example.bankcards.dto.BankCardResponse;
import com.example.bankcards.service.BankCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            @RequestBody @Valid BankCardRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        BankCardResponse response = bankCardService.createCard(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BankCardResponse>> getCards(
            Authentication authentication,
            @RequestParam(required = false) Boolean active) {

        String username = authentication.getName();
        List<BankCardResponse> cards = bankCardService.getCards(username, active);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{cardId}/toggle")
    public ResponseEntity<Void> toggleCardStatus(@PathVariable Long cardId, Principal principal) {
        bankCardService.toggleCardStatus(cardId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BankCardResponse>> getFilteredCards(
            @RequestParam boolean active,
            Authentication authentication) {

        String username = authentication.getName();
        List<BankCardResponse> cards = bankCardService.getCardsByActiveStatus(username, active);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<BankCardResponse> updateCard(
            @PathVariable Long cardId,
            @RequestBody BankCardRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        BankCardResponse updatedCard = bankCardService.updateCard(cardId, request, username);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId, Principal principal) {
        bankCardService.deleteCard(cardId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<BankCardResponse>> getCardsByPage(
            @RequestParam boolean active,
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication) {

        String username = authentication.getName();
        Page<BankCardResponse> cards = bankCardService.getCardsByPage(username, active, page, size);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<BankCardResponse>> getLatestCards(Authentication authentication) {
        String username = authentication.getName();
        List<BankCardResponse> latestCards = bankCardService.getLatestCards(username);
        return ResponseEntity.ok(latestCards);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<BankCardResponse>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BankCardResponse> cardsPage = bankCardService.getAllCards(pageable);
        return ResponseEntity.ok(cardsPage);
    }

}

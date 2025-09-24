package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardRequest;
import com.example.bankcards.dto.BankCardResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.model.User;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class BankCardService {

    @Autowired
    private BankCardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    public BankCardResponse createCard(BankCardRequest request, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        BankCard card = new BankCard();
        card.setCardNumber(request.getCardNumber());
        card.setExpirationDate(request.getExpirationDate());
        card.setCvv(request.getCvv());
        card.setUser(user);

        cardRepository.save(card);

        return mapToResponse(card);
    }

    public List<BankCardResponse> getCards(String username) {
        return cardRepository.findByUserUsername(username)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BankCardResponse mapToResponse(BankCard card) {
        BankCardResponse response = new BankCardResponse();
        response.setId(card.getId());
        response.setCardNumber(card.getCardNumber());
        response.setCardHolder(card.getCardHolder());
        response.setExpirationDate(card.getExpirationDate());
        response.setActive(card.isActive());
        return response;
    }

    public void toggleCardStatus(Long cardId, String username) {
        BankCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        if (!card.getUser().getUsername().equals(username)) {
            try {
                throw new AccessDeniedException("You do not own this card");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        card.setActive(!card.isActive());
        cardRepository.save(card);
    }

}

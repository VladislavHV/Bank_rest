package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardRequest;
import com.example.bankcards.dto.BankCardResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

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
        card.setCardHolder(request.getCardHolder());
        card.setExpirationDate(request.getExpirationDate());
        card.setCvv(request.getCvv());
        card.setActive(true);
        card.setUser(user);

        cardRepository.save(card);

        return mapToResponse(card);
    }

    public List<BankCardResponse> getCards(String username, Boolean active) {
        List<BankCard> cards;

        if (active != null) {
            cards = cardRepository.findByUserUsernameAndActive(username, active);
        } else {
            cards = cardRepository.findByUserUsername(username);
        }

        return cards.stream()
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

    public List<BankCardResponse> getCardsByActiveStatus(String username, boolean active) {
        return cardRepository.findByUserUsernameAndActive(username, active)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BankCardResponse updateCard(Long cardId, BankCardRequest request, String username) {
        BankCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        if (!card.getUser().getUsername().equals(username)) {
            try {
                throw new AccessDeniedException("You do not own this card");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        card.setCardNumber(request.getCardNumber());
        card.setExpirationDate(request.getExpirationDate());
        card.setCvv(request.getCvv());
        card.setCardHolder(request.getCardHolder());

        cardRepository.save(card);

        return mapToResponse(card);
    }

    public void deleteCard(Long cardId, String username) {
        BankCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        if (!card.getUser().getUsername().equals(username)) {
            try {
                throw new AccessDeniedException("You do not own this card");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        cardRepository.delete(card);
    }

    public Page<BankCardResponse> getCardsByPage(String username, boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByUserUsernameAndActive(username, active, pageable)
                .map(this::mapToResponse);
    }

    public List<BankCardResponse> getLatestCards(String username) {
        return cardRepository.findTop5ByUserUsernameOrderByIdDesc(username)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<BankCardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

}

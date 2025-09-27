package com.example.bankcards.service;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BankCardServiceTest {

    @Mock
    private BankCardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BankCardService bankCardService;

    public BankCardServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToggleCardStatus_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        BankCard card = new BankCard();
        card.setId(1L);
        card.setUser(user);
        card.setActive(false);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        bankCardService.toggleCardStatus(1L, "testuser");

        assertTrue(card.isActive());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    public void testToggleCardStatus_Unauthorized() {
        User user = new User();
        user.setUsername("owner");

        BankCard card = new BankCard();
        card.setId(2L);
        card.setUser(user);

        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bankCardService.toggleCardStatus(2L, "anotherUser")
        );

        assertTrue(exception.getCause().getMessage().contains("You do not own this card"));
        verify(cardRepository, never()).save(any());
    }

    @Test
    public void testToggleCardStatus_CardNotFound() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                bankCardService.toggleCardStatus(99L, "testuser")
        );
    }

}

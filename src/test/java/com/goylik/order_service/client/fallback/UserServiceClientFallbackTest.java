package com.goylik.order_service.client.fallback;

import com.goylik.order_service.exception.client.UserServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceClientFallbackTest {

    private UserServiceClientFallback fallback;

    @BeforeEach
    void setUp() {
        fallback = new UserServiceClientFallback();
    }

    @Test
    void getUserByIdInternal_ShouldThrowUserServiceUnavailableException() {
        assertThrows(
                UserServiceUnavailableException.class,
                () -> fallback.getUserByIdInternal(1L)
        );
    }

    @Test
    void getUserByIdInternal_ShouldThrowWithCorrectMessage() {
        UserServiceUnavailableException ex = assertThrows(
                UserServiceUnavailableException.class,
                () -> fallback.getUserByIdInternal(1L)
        );

        assertEquals("User service is unavailable.", ex.getMessage());
    }
}

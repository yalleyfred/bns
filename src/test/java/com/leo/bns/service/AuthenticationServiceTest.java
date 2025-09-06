package com.leo.bns.service;

import com.leo.bns.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private Environment env;

    @InjectMocks
    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        when(env.getProperty("API_PASSWORD")).thenReturn("correct_password");
    }

    @Test
    void validateApiPassword_ValidPassword_ShouldPass() {
        assertDoesNotThrow(() -> authService.validateApiPassword("correct_password"));
    }

    @Test
    void validateApiPassword_InvalidPassword_ShouldThrowException() {
        assertThrows(UnauthorizedException.class, 
                () -> authService.validateApiPassword("wrong_password"));
    }

    @Test
    void validateApiPassword_EmptyPassword_ShouldThrowException() {
        assertThrows(UnauthorizedException.class, 
                () -> authService.validateApiPassword(""));
    }

    @Test
    void validateApiPassword_NullPassword_ShouldThrowException() {
        assertThrows(UnauthorizedException.class, 
                () -> authService.validateApiPassword(null));
    }
}

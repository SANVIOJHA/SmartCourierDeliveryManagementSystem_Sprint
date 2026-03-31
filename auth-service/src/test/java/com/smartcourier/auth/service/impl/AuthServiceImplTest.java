package com.smartcourier.auth.service.impl;

import com.smartcourier.auth.dto.AuthRequest;
import com.smartcourier.auth.entity.User;
import com.smartcourier.auth.exception.AuthException;
import com.smartcourier.auth.repository.UserRepository;
import com.smartcourier.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signupShouldDefaultRoleToCustomerWhenRoleMissing() {
        // Missing roles should still create a valid CUSTOMER account for gateway-driven signup.
        AuthRequest request = new AuthRequest();
        request.setUsername("alice");
        request.setPassword("secret");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        String result = authService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("User registered successfully", result);
        assertEquals("alice", userCaptor.getValue().getUsername());
        assertEquals("encoded-secret", userCaptor.getValue().getPassword());
        assertEquals("CUSTOMER", userCaptor.getValue().getRole());
    }

    @Test
    void loginShouldThrowWhenPasswordDoesNotMatch() {
        // Login must fail before any token is created when the password is wrong.
        AuthRequest request = new AuthRequest();
        request.setUsername("alice");
        request.setPassword("wrong-password");

        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded-secret");
        user.setRole("CUSTOMER");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-secret")).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(request));
        assertEquals("Invalid credentials", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void loginShouldReturnJwtWhenCredentialsMatch() {
        // A valid login should delegate token generation to JwtUtil with the persisted role.
        AuthRequest request = new AuthRequest();
        request.setUsername("alice");
        request.setPassword("secret");

        User user = new User();
        user.setUsername("alice");
        user.setPassword("encoded-secret");
        user.setRole("ADMIN");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(jwtUtil.generateToken("alice", "ADMIN")).thenReturn("jwt-token");

        String token = authService.login(request);

        assertEquals("jwt-token", token);
        verify(jwtUtil, times(1)).generateToken("alice", "ADMIN");
    }
}

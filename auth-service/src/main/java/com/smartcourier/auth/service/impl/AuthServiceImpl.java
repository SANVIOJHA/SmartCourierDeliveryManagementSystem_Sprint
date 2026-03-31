package com.smartcourier.auth.service.impl;

import com.smartcourier.auth.dto.AuthRequest;
import com.smartcourier.auth.dto.RoleUpdateRequest;
import com.smartcourier.auth.dto.UserUpdateRequest;
import com.smartcourier.auth.entity.User;
import com.smartcourier.auth.exception.AuthException;
import com.smartcourier.auth.exception.UserNotFoundException;
import com.smartcourier.auth.repository.UserRepository;
import com.smartcourier.auth.security.JwtUtil;
import com.smartcourier.auth.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String signup(AuthRequest request) {
        repo.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new AuthException("Username already exists");
        });

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String role = request.getRole();
        user.setRole((role == null || role.isBlank()) ? "CUSTOMER" : role.trim().toUpperCase(Locale.ROOT));

        repo.save(user);
        return "User registered successfully";
    }

    @Override
    public String login(AuthRequest request) {
        User user = repo.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
    }

    @Override
    public User getUserByUsername(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found for username: " + username));
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return repo.findByRoleIgnoreCase(role);
    }

    @Override
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername().trim());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(request.getRole().trim().toUpperCase(Locale.ROOT));
        }

        return repo.save(user);
    }

    @Override
    public User updateUserRole(Long id, RoleUpdateRequest request) {
        User user = getUserById(id);
        user.setRole(request.getRole().trim().toUpperCase(Locale.ROOT));
        return repo.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        repo.delete(user);
    }

    @Override
    public long countUsers() {
        return repo.count();
    }
}

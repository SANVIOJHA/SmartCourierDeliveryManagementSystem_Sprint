package com.smartcourier.auth.controller;

import com.smartcourier.auth.dto.AuthRequest;
import com.smartcourier.auth.dto.AuthResponse;
import com.smartcourier.auth.dto.RoleUpdateRequest;
import com.smartcourier.auth.dto.UserUpdateRequest;
import com.smartcourier.auth.entity.User;
import com.smartcourier.auth.exception.AuthException;
import com.smartcourier.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartcourier.auth.security.JwtUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestHeader("Authorization") String header) {
        String token = extractToken(header);
        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new AuthException("Access denied");
        }
        return service.getAllUsers();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(new AuthResponse(service.login(request)));
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @GetMapping("/users/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return service.getUserByUsername(username);
    }

    @GetMapping("/users/role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        return service.getUsersByRole(role);
    }

    @GetMapping("/users/count")
    public long countUsers() {
        return service.countUsers();
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return "User deleted successfully";
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return service.updateUser(id, request);
    }

    @PutMapping("/users/{id}/role")
    public User updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return service.updateUserRole(id, request);
    }

    @GetMapping("/validate")
    public Map<String, Object> validate(@RequestHeader("Authorization") String header) {
        String token = extractToken(header);
        return Map.of(
                "username", jwtUtil.extractUsername(token),
                "role", jwtUtil.extractRole(token),
                "valid", true
        );
    }

    @GetMapping("/role")
    public String role(@RequestHeader("Authorization") String header) {
        return jwtUtil.extractRole(extractToken(header));
    }

    @GetMapping("/me")
    public Map<String, String> me(@RequestHeader("Authorization") String header) {
        String token = extractToken(header);
        return Map.of(
                "username", jwtUtil.extractUsername(token),
                "role", jwtUtil.extractRole(token)
        );
    }

    @PostMapping("/logout")
    public String logout() {
        return "Logged out successfully";
    }

    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AuthException("Missing or invalid Authorization header");
        }
        return header.substring(7);
    }
}

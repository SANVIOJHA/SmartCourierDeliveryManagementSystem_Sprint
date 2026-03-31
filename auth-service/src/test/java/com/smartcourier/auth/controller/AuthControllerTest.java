package com.smartcourier.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourier.auth.dto.AuthRequest;
import com.smartcourier.auth.dto.RoleUpdateRequest;
import com.smartcourier.auth.dto.UserUpdateRequest;
import com.smartcourier.auth.entity.User;
import com.smartcourier.auth.exception.GlobalExceptionHandler;
import com.smartcourier.auth.security.JwtUtil;
import com.smartcourier.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, jwtUtil))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUsersShouldReturnAllUsersForAdmin() throws Exception {
        when(jwtUtil.extractRole("token")).thenReturn("ADMIN");
        when(authService.getAllUsers()).thenReturn(List.of(user(1L, "alice", "CUSTOMER")));

        mockMvc.perform(get("/auth/users").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void signupShouldReturnSuccessMessage() throws Exception {
        when(authService.signup(any(AuthRequest.class))).thenReturn("User registered successfully");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest("alice", "secret", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User registered successfully"));
    }

    @Test
    void loginShouldReturnToken() throws Exception {
        when(authService.login(any(AuthRequest.class))).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest("alice", "secret", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void getUserShouldReturnUserById() throws Exception {
        when(authService.getUserById(1L)).thenReturn(user(1L, "alice", "CUSTOMER"));

        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getUserByUsernameShouldReturnUser() throws Exception {
        when(authService.getUserByUsername("alice")).thenReturn(user(1L, "alice", "CUSTOMER"));

        mockMvc.perform(get("/auth/users/username/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void getUsersByRoleShouldReturnMatchingUsers() throws Exception {
        when(authService.getUsersByRole("ADMIN")).thenReturn(List.of(user(2L, "admin", "ADMIN")));

        mockMvc.perform(get("/auth/users/role/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void countUsersShouldReturnCount() throws Exception {
        when(authService.countUsers()).thenReturn(3L);

        mockMvc.perform(get("/auth/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    void deleteUserShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User deleted successfully"));
    }

    @Test
    void updateUserShouldReturnUpdatedUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("alice-updated");
        when(authService.updateUser(eq(1L), any(UserUpdateRequest.class)))
                .thenReturn(user(1L, "alice-updated", "CUSTOMER"));

        mockMvc.perform(put("/auth/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice-updated"));
    }

    @Test
    void updateRoleShouldReturnUpdatedRole() throws Exception {
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("ADMIN");
        when(authService.updateUserRole(eq(1L), any(RoleUpdateRequest.class)))
                .thenReturn(user(1L, "alice", "ADMIN"));

        mockMvc.perform(put("/auth/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void validateShouldReturnDecodedTokenData() throws Exception {
        when(jwtUtil.extractUsername("token")).thenReturn("alice");
        when(jwtUtil.extractRole("token")).thenReturn("CUSTOMER");

        mockMvc.perform(get("/auth/validate").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void roleShouldReturnRoleFromToken() throws Exception {
        when(jwtUtil.extractRole("token")).thenReturn("ADMIN");

        mockMvc.perform(get("/auth/role").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("ADMIN"));
    }

    @Test
    void meShouldReturnCurrentUserDetails() throws Exception {
        when(jwtUtil.extractUsername("token")).thenReturn("alice");
        when(jwtUtil.extractRole("token")).thenReturn("CUSTOMER");

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void logoutShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Logged out successfully"));
    }

    private AuthRequest authRequest(String username, String password, String role) {
        AuthRequest request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRole(role);
        return request;
    }

    private User user(Long id, String username, String role) {
        return new User(id, username, "encoded", role);
    }
}

package com.smartcourier.auth.service;

import com.smartcourier.auth.dto.AuthRequest;
import com.smartcourier.auth.dto.RoleUpdateRequest;
import com.smartcourier.auth.dto.UserUpdateRequest;
import com.smartcourier.auth.entity.User;

import java.util.List;

public interface AuthService {

    String signup(AuthRequest request);

    String login(AuthRequest request);

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    List<User> getUsersByRole(String role);

    User updateUser(Long id, UserUpdateRequest request);

    User updateUserRole(Long id, RoleUpdateRequest request);

    void deleteUser(Long id);

    long countUsers();
}

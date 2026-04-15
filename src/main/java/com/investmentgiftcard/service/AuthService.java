package com.investmentgiftcard.service;

import com.investmentgiftcard.dto.request.LoginRequest;
import com.investmentgiftcard.dto.request.RegisterRequest;
import com.investmentgiftcard.dto.response.AuthResponse;

/**
 * Authentication service interface.
 */
public interface AuthService {

    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate user and return tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * Logout user (invalidate tokens)
     */
    void logout(String token);
}

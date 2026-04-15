package com.investmentgiftcard.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmentgiftcard.dto.response.ApiResponse;
import com.investmentgiftcard.dto.response.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point for handling unauthorized access attempts.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        log.error("Unauthorized error: {} - {}", authException.getMessage(), request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .code("UNAUTHORIZED")
                .type("AuthenticationException")
                .path(request.getRequestURI())
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.error(
                "Authentication required. Please provide a valid token.",
                errorDetails
        );

        objectMapper.findAndRegisterModules();
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}

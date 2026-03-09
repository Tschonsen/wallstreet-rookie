package com.wallstreetrookie.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallstreetrookie.backend.dto.request.LoginRequest;
import com.wallstreetrookie.backend.dto.request.RegisterRequest;
import com.wallstreetrookie.backend.dto.response.AuthResponse;
import com.wallstreetrookie.backend.security.JwtAuthenticationFilter;
import com.wallstreetrookie.backend.security.JwtTokenProvider;
import com.wallstreetrookie.backend.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlayerService playerService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_validRequest_shouldReturn200() throws Exception {
        AuthResponse authResponse = new AuthResponse("jwt-token", "testuser", Instant.now().plusSeconds(86400));
        when(playerService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("testuser", "password123", "test@mail.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_emptyUsername_shouldReturn400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("", "password123", "test@mail.com"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shortPassword_shouldReturn400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("testuser", "short", "test@mail.com"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidEmail_shouldReturn400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("testuser", "password123", "not-an-email"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_validRequest_shouldReturn200() throws Exception {
        AuthResponse authResponse = new AuthResponse("jwt-token", "testuser", Instant.now().plusSeconds(86400));
        when(playerService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("testuser", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_emptyUsername_shouldReturn400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("", "password123"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_emptyPassword_shouldReturn400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("testuser", ""))))
                .andExpect(status().isBadRequest());
    }
}

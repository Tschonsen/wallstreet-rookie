package com.wallstreetrookie.backend.service;

import com.wallstreetrookie.backend.dto.request.LoginRequest;
import com.wallstreetrookie.backend.dto.request.RegisterRequest;
import com.wallstreetrookie.backend.dto.response.AuthResponse;
import com.wallstreetrookie.backend.model.Player;

public interface PlayerService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    Player getCurrentPlayer(String username);
}

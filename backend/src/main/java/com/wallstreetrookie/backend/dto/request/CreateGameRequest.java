package com.wallstreetrookie.backend.dto.request;

import com.wallstreetrookie.backend.model.enums.GameMode;
import com.wallstreetrookie.backend.model.GameSettings;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequest(
        @NotNull(message = "Spielmodus darf nicht leer sein")
        GameMode mode,

        GameSettings settings
) {}

package com.wallstreetrookie.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username darf nicht leer sein")
        @Size(min = 3, max = 20, message = "Username muss zwischen 3 und 20 Zeichen lang sein")
        String username,

        @NotBlank(message = "Passwort darf nicht leer sein")
        @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen lang sein")
        String password,

        @NotBlank(message = "Email darf nicht leer sein")
        @Email(message = "Ungültige Email-Adresse")
        String email
) {}

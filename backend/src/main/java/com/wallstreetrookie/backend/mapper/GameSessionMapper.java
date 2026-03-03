package com.wallstreetrookie.backend.mapper;

import com.wallstreetrookie.backend.dto.response.GameSessionResponse;
import com.wallstreetrookie.backend.model.GameSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameSessionMapper {

    GameSessionResponse toResponse(GameSession gameSession);
}

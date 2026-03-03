package com.wallstreetrookie.backend.mapper;

import com.wallstreetrookie.backend.dto.response.PlayerResponse;
import com.wallstreetrookie.backend.model.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerResponse toResponse(Player player);
}

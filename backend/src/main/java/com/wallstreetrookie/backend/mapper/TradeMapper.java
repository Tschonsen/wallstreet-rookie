package com.wallstreetrookie.backend.mapper;

import com.wallstreetrookie.backend.dto.response.TradeResponse;
import com.wallstreetrookie.backend.model.Trade;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeMapper {

    TradeResponse toResponse(Trade trade);
}

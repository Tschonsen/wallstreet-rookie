package com.wallstreetrookie.backend.mapper;

import com.wallstreetrookie.backend.dto.response.StockResponse;
import com.wallstreetrookie.backend.model.StockModel;
import com.wallstreetrookie.backend.model.StockPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "stock.symbol", target = "symbol")
    @Mapping(source = "stock.name", target = "name")
    @Mapping(source = "stock.sector", target = "sector")
    @Mapping(source = "price.price", target = "price")
    @Mapping(source = "price.change", target = "change")
    @Mapping(source = "price.changePercent", target = "changePercent")
    StockResponse toResponse(StockModel stock, StockPrice price);
}

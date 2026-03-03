package com.wallstreetrookie.backend.mapper;

import com.wallstreetrookie.backend.dto.response.NewsResponse;
import com.wallstreetrookie.backend.model.News;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    NewsResponse toResponse(News news);
}

package org.example.mapper;

import org.example.dto.AdsDto;
import org.example.model.Ads;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdsMapper {
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    AdsDto toDto(Ads ads);

    List<AdsDto> toDtoList(List<Ads> adsList);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    Ads toEntity(AdsDto adsDto);

    List<Ads> toEntityList(List<AdsDto> adsDtoList);
}

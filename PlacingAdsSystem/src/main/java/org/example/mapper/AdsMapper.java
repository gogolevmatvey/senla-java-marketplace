package org.example.mapper;

import org.example.dto.AdsDto;
import org.example.model.Ads;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdsMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "sellerRating", source = "user.sellerRating")
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "promoted", source = "promoted")
    @Mapping(target = "promotionStartDate", source = "promotionStartDate")
    @Mapping(target = "promotionEndDate", source = "promotionEndDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "mainImage", source = "mainImage")
    AdsDto toDto(Ads ads);

    List<AdsDto> toDtoList(List<Ads> adsList);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    Ads toEntity(AdsDto adsDto);

    List<Ads> toEntityList(List<AdsDto> adsDtoList);
}

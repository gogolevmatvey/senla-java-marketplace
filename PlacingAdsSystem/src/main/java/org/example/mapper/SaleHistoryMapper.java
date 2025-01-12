package org.example.mapper;

import org.example.dto.SaleHistoryDto;
import org.example.model.SaleHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleHistoryMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "adsTitle", source = "ads.title")
    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "buyerUsername", source = "buyer.username")
    @Mapping(target = "saleDate", source = "saleDate")
    @Mapping(target = "salePrice", source = "salePrice")
    SaleHistoryDto toDto(SaleHistory saleHistory);

    List<SaleHistoryDto> toDtoList(List<SaleHistory> saleHistories);
}

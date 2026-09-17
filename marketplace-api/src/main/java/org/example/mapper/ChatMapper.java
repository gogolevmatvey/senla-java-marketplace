package org.example.mapper;

import org.example.dto.ChatDto;
import org.example.model.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MessageMapper.class})
public interface ChatMapper {
    @Mapping(target = "adsId", source = "ads.id")
    @Mapping(target = "adsTitle", source = "ads.title")
    @Mapping(target = "buyerUsername", source = "buyer.username")
    @Mapping(target = "sellerUsername", source = "ads.user.username")
    @Mapping(target = "messages", source = "messages")
    ChatDto toDto(Chat chat);
}

package org.example.mapper;

import org.example.dto.MessageDto;
import org.example.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "senderUsername", source = "sender.username")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "sendDate", source = "sendDate")
    MessageDto toDto(Message message);
}

package org.example.mapper;

import org.example.dto.CommentDto;
import org.example.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "adsId", source = "ads.id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "creationDate", source = "creationDate")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> commentList);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "rating", source = "rating")
    Comment toEntity(CommentDto commentDto);

    List<Comment> toEntityList(List<CommentDto> commentDtoList);
}

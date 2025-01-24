package org.example.mapper;

import org.example.dto.UserDto;
import org.example.model.Ads;
import org.example.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "sellerRating", source = "sellerRating")
    @Mapping(target = "advertisementIds", source = "advertisements", qualifiedByName = "adsToIds")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "profilePicture", source = "profilePicture")
    UserDto toDto(User user);

    @Named("adsToIds")
    default List<Long> adsToIds(List<Ads> ads) {
        if (ads == null) return null;
        return ads.stream()
                .map(Ads::getId)
                .collect(Collectors.toList());
    }

    List<UserDto> toDtoList(List<User> users);
}

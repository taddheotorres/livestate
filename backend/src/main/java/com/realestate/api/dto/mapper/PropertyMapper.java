package com.realestate.api.dto.mapper;

import com.realestate.api.dto.request.PropertyRequest;
import com.realestate.api.dto.response.ImageResponse;
import com.realestate.api.dto.response.PropertyResponse;
import com.realestate.api.dto.response.UserSummary;
import com.realestate.api.model.Property;
import com.realestate.api.model.PropertyImage;
import com.realestate.api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "agent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Property toEntity(PropertyRequest request);

    @Mapping(target = "agent", source = "agent", qualifiedByName = "toUserSummary")
    @Mapping(target = "images", source = "images", qualifiedByName = "toImageResponseList")
    PropertyResponse toResponse(Property property);

    List<PropertyResponse> toResponseList(List<Property> properties);

    @Named("toUserSummary")
    default UserSummary toUserSummary(User user) {
        if (user == null) return null;
        return UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rating(user.getRating())
                .reviewsCount(user.getReviewsCount())
                .bio(user.getBio())
                .phone(user.getPhone())
                .build();
    }

    @Named("toImageResponse")
    default ImageResponse toImageResponse(PropertyImage image) {
        if (image == null) return null;
        return ImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.isPrimary())
                .build();
    }

    @Named("toImageResponseList")
    default List<ImageResponse> toImageResponseList(List<PropertyImage> images) {
        if (images == null) return List.of();
        return images.stream().map(this::toImageResponse).toList();
    }
}

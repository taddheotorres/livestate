package com.realestate.api.dto.mapper;

import com.realestate.api.dto.response.BookingResponse;
import com.realestate.api.dto.response.PropertySummary;
import com.realestate.api.dto.response.UserSummary;
import com.realestate.api.model.Booking;
import com.realestate.api.model.Property;
import com.realestate.api.model.PropertyImage;
import com.realestate.api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "property", source = "property", qualifiedByName = "toPropertySummary")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "toUserSummary")
    @Mapping(target = "status", expression = "java(booking.getStatus().name())")
    @Mapping(target = "paymentMethod", expression = "java(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null)")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);

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

    @Named("toPropertySummary")
    default PropertySummary toPropertySummary(Property property) {
        if (property == null) return null;
        String imageUrl = null;
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            imageUrl = property.getImages().stream()
                    .filter(PropertyImage::isPrimary)
                    .findFirst()
                    .map(PropertyImage::getImageUrl)
                    .orElse(property.getImages().get(0).getImageUrl());
        }
        return PropertySummary.builder()
                .id(property.getId())
                .title(property.getTitle())
                .location(property.getLocation())
                .price(property.getPrice())
                .imageUrl(imageUrl)
                .build();
    }
}

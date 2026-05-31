package com.realestate.api.dto.mapper;

import com.realestate.api.dto.response.UserSummary;
import com.realestate.api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserSummary toSummary(User user);

    List<UserSummary> toSummaryList(List<User> users);
}

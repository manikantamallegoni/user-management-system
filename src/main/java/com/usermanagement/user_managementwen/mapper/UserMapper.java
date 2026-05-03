package com.usermanagement.user_managementwen.mapper;

import com.usermanagement.user_managementwen.dto.UserDto;
import com.usermanagement.user_managementwen.entity.User;

public class UserMapper {
    // covert user jpa Entity into user dto
    public static UserDto maptoUserDto(User user) {
        UserDto userDto=new UserDto( user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword());
        return userDto;

    }
    // covert user dto into user jpa entity
    public static User maptoUser(UserDto userDto) {
        User user=new User(
                userDto.getId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword()
        );
        return user;
    }
}

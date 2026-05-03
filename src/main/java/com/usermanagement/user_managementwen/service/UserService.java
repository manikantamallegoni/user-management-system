package com.usermanagement.user_managementwen.service;

import com.usermanagement.user_managementwen.dto.UserDto;

import java.util.List;
public interface UserService  {
    UserDto createUser(UserDto user);
    UserDto getUserByID(Long userId);
    List<UserDto>getAllUsers();
    UserDto updateUser(UserDto user);



    void deleteUser(Long userId);
    UserDto login(String email, String password);



}

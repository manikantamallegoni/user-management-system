package com.usermanagement.user_managementwen.service.impl;

import com.usermanagement.user_managementwen.dto.UserDto;
import com.usermanagement.user_managementwen.entity.User;
import com.usermanagement.user_managementwen.exception.ResourceNotFoundEception;
import com.usermanagement.user_managementwen.mapper.UserMapper;
import com.usermanagement.user_managementwen.repository.UserRepository;
import com.usermanagement.user_managementwen.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class UserServiceImpl implements UserService {
private UserRepository userRepository;
private ModelMapper modelMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        //covert USer dto into user jpa entity
     // User user= UserMapper.maptoUser(userDto);
        User user= modelMapper.map(userDto,User.class);


        User savedUser= userRepository.save(user);
        // covert User jpa entity to userdto
     //UserDto savedUserDto=UserMapper.maptoUserDto(savedUser);


        UserDto savedUserDto=modelMapper.map(savedUser,UserDto.class);


        return savedUserDto;

    }

    @Override
    public UserDto getUserByID(Long userId) {
        User user =userRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFoundEception("User","id",userId)
        );

       //return   UserMapper.maptoUserDto(user);
        return  modelMapper.map(user,UserDto.class);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        //return users.stream().map(UserMapper::maptoUserDto).collect(Collectors.toList());
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }



    @Override
    public UserDto updateUser(UserDto user) {
        User exsistingUser=userRepository.findById(user.getId()).orElseThrow(
                ()-> new ResourceNotFoundEception("User","id",user.getId())
        );
        exsistingUser.setFirstName(user.getFirstName());
        exsistingUser.setLastName(user.getLastName());
        exsistingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            exsistingUser.setPassword(user.getPassword());
        }
        User updatedUser=userRepository.save(exsistingUser);


//     return  UserMapper.maptoUserDto(updatedUser);
        return  modelMapper.map(updatedUser,UserDto.class);

    }



    @Override
    public void deleteUser(Long userId) {
        User exsistingUser=userRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFoundEception("User","id",userId)
        );


        userRepository.deleteById(userId);


    }

    @Override
    public UserDto login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundEception("User", "email", email)
        );

        if (user.getPassword().equals(password)) {
            return modelMapper.map(user, UserDto.class);
        } else {
            throw new RuntimeException("Invalid password");
        }
    }
}

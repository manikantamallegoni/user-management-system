package com.usermanagement.user_managementwen.controller;


import ch.qos.logback.classic.spi.ILoggingEvent;
import com.usermanagement.user_managementwen.dto.UserDto;
import com.usermanagement.user_managementwen.entity.User;
import com.usermanagement.user_managementwen.exception.ErrorDeatails;
import com.usermanagement.user_managementwen.exception.ResourceNotFoundEception;
import com.usermanagement.user_managementwen.repository.UserRepository;
import com.usermanagement.user_managementwen.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.aspectj.bridge.IMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")

public class UserController {
    @Autowired
    private UserService userService;

    //build create user rest api

    @PostMapping
    public ResponseEntity<UserDto>createUser(@Valid @RequestBody UserDto user){
        UserDto savedUser=userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    //build get user by id rest api
    //http://localhost:8080/api/users/1
    @GetMapping("{id}")
    public ResponseEntity<UserDto>getUserByID(@PathVariable("id") Long userId){
        UserDto userDto=userService.getUserByID(userId);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    //buid get all user rest api
    //http://localhost:8080/api/users
    @GetMapping
    public ResponseEntity<List<UserDto>>getAllUsers(){
        List<UserDto> users=userService.getAllUsers();
        return new ResponseEntity<>(users,HttpStatus.OK);
    }
    //build update user rest api
    //http://localhost:8080/api/users/1
    @PutMapping("{id}")
    public ResponseEntity<UserDto>updateUser(@PathVariable("id")Long  userId,@RequestBody @Valid  UserDto user){
        user.setId(userId);
        UserDto updatedUser=userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
//build delete user rest api
    @DeleteMapping("{id}")
    public ResponseEntity<String>deleteUser(@PathVariable("id") Long  userId){
        userService.deleteUser(userId);
        return new ResponseEntity<>( "User successfully deleted",HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        try {
            UserDto userDto = userService.login(email, password);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @ExceptionHandler(ResourceNotFoundEception.class)
    public ResponseEntity<ErrorDeatails>resourceNotFoundExcepion(ResourceNotFoundEception exception, WebRequest webRequest){

        String errorCode;
        ErrorDeatails errorDeatails = new ErrorDeatails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "User_Not_Found"
        );


        return new ResponseEntity<>(errorDeatails, HttpStatus.NOT_FOUND);

    }

}

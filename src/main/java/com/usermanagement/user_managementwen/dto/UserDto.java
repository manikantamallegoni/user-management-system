package com.usermanagement.user_managementwen.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    //user first name should not be empty
    @NotEmpty(message = "User first name should not be empty")
    private String firstName;
    // user last name should not be empty
    @NotEmpty(message = "User last name should not be empty")
    private String lastName;
    //usereamial should be valid and not empty
    @NotEmpty(message = "User email should not be empty")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password should not be empty")
    private String password;

}

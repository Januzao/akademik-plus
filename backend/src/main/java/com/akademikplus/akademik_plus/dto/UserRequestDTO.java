package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User request payload")
public class UserRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;
    private String pesel;
    private String countryOfOrigin;
    private String disability;
    private String personalPreferences;
    private Role role;
    private String profilePhoto;
    private Boolean isActive;
    private Long roomId;
}
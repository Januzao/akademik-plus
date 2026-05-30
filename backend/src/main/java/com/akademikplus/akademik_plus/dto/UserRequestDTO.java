package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User request payload")
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String pesel;
    private String countryOfOrigin;
    private String disability;
    private String personalPreferences;
    private String role;
    private String profilePhoto;
    private Boolean isActive;
    private Long roomId;
}
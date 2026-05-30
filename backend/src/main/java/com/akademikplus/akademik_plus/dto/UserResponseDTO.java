package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User response payload")
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String pesel;
    private String countryOfOrigin;
    private String disability;
    private String personalPreferences;
    private String phone;
    private String role;
    private Boolean isActive;
    private String profilePhoto;
    private Long roomId;
}

package com.akademikplus.akademik_plus.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer id;
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
    private Integer roomId;
}

package com.akademikplus.akademik_plus.dto;

import lombok.Data;

@Data
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
    private Integer roomId;
}
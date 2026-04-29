package com.akademikplus.akademik_plus.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private Boolean isActive;
    private String profilePhoto;
    private Integer roomId;
}

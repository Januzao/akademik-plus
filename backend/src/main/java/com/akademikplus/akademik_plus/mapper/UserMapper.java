package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setPesel(user.getPesel());
        dto.setCountryOfOrigin(user.getCountryOfOrigin());
        dto.setDisability(user.getDisability());
        dto.setPersonalPreferences(user.getPersonalPreferences());

        dto.setProfilePhoto(user.getProfilePhoto());

        dto.setBalance(user.getBalance());

        if (user.getRoom() != null) {
            dto.setRoomId(user.getRoom().getId());
        }
        return dto;
    }

    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setProfilePhoto(dto.getProfilePhoto());
        user.setPesel(dto.getPesel());
        user.setDisability(dto.getDisability());
        user.setCountryOfOrigin(dto.getCountryOfOrigin());
        user.setPersonalPreferences(dto.getPersonalPreferences());

        user.setPasswordHash(dto.getPassword());

        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        return user;
    }
}

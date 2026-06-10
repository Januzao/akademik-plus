package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "User response payload")
public class UserResponseDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "First name", example = "Jan")
    private String firstName;

    @Schema(description = "Last name", example = "Kowalski")
    private String lastName;

    @Schema(description = "Email address", example = "jan.kowalski@example.com")
    private String email;

    @Schema(description = "PESEL number", example = "99010112345")
    private String pesel;

    @Schema(description = "Country of origin", example = "Poland")
    private String countryOfOrigin;

    @Schema(description = "Disability status or description")
    private String disability;

    @Schema(description = "Personal preferences")
    private String personalPreferences;

    @Schema(description = "Phone number", example = "+48 500 100 200")
    private String phone;

    @Schema(description = "User role", example = "STUDENT")
    private Role role;

    @Schema(description = "Whether the account is active", example = "true")
    private Boolean isActive;

    @Schema(description = "URL to profile photo", example = "/uploads/users/abc123.jpg")
    private String profilePhoto;

    @Schema(description = "ID of assigned room", example = "3")
    private Long roomId;

    @Schema(description = "Current account balance in PLN", example = "350.00")
    private BigDecimal balance;
}

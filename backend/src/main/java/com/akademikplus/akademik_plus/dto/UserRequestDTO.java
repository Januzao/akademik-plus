package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User creation / update payload")
public class UserRequestDTO {

    @Schema(description = "First name", example = "Jan")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last name", example = "Kowalski")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Email address — must be unique", example = "jan.kowalski@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password — minimum 6 characters", example = "secret123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "Phone number", example = "+48 500 100 200")
    private String phone;

    @Schema(description = "PESEL number", example = "99010112345")
    private String pesel;

    @Schema(description = "Country of origin", example = "Poland")
    private String countryOfOrigin;

    @Schema(description = "Disability status or description")
    private String disability;

    @Schema(description = "Personal preferences (dietary, allergies, etc.)")
    private String personalPreferences;

    @Schema(description = "User role", example = "STUDENT")
    private Role role;

    @Schema(description = "URL to profile photo")
    private String profilePhoto;

    @Schema(description = "Whether the account is active", example = "true")
    private Boolean isActive;

    @Schema(description = "ID of the assigned room", example = "3")
    private Long roomId;
}

package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registration payload")
public class RegisterRequestDTO {

    @Schema(description = "Email address", example = "jan.kowalski@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Password — minimum 6 characters", example = "secret123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "First name", example = "Jan")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last name", example = "Kowalski")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Phone number", example = "+48 500 100 200")
    private String phone;
}

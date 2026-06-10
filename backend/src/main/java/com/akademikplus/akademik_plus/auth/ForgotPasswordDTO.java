package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Forgot password request — triggers a reset email")
public class ForgotPasswordDTO {

    @Schema(description = "Email address associated with the account", example = "jan.kowalski@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}

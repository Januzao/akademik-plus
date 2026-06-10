package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Reset password payload — token received by email")
public class ResetPasswordDTO {

    @Schema(description = "Reset token received by email", example = "a1b2c3d4-e5f6-...")
    @NotBlank(message = "Reset token is required")
    private String token;

    @Schema(description = "New password — minimum 8 characters", example = "newSecret456")
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;
}

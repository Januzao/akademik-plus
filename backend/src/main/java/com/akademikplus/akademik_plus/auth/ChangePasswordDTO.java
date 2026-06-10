package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Change password payload — requires authentication")
public class ChangePasswordDTO {

    @Schema(description = "Current password", example = "oldSecret123")
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password — minimum 8 characters", example = "newSecret456")
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;
}

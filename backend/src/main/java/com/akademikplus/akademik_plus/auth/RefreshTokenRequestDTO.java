package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Refresh token request — exchange a refresh token for a new access token")
public class RefreshTokenRequestDTO {

    @Schema(description = "Refresh token obtained at login", example = "a1b2c3d4-e5f6-...")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

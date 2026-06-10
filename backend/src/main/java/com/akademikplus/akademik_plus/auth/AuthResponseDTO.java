package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response — contains access and refresh tokens")
public class AuthResponseDTO {

    @Schema(description = "Short-lived JWT access token for API requests", example = "eyJhbGci...")
    private String token;

    @Schema(description = "Long-lived refresh token (7 days) — use to obtain a new access token", example = "a1b2c3d4-...")
    private String refreshToken;
}

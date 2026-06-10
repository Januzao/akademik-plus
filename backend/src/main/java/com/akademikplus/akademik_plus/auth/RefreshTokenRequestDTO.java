package com.akademikplus.akademik_plus.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

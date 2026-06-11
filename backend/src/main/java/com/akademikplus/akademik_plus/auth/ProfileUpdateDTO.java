package com.akademikplus.akademik_plus.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Student own profile update payload")
public class ProfileUpdateDTO {

    @Schema(description = "First name", example = "Jan")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last name", example = "Kowalski")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Phone number", example = "+48 500 100 200")
    private String phone;
}

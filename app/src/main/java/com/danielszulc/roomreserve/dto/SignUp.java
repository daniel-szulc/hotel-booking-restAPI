package com.danielszulc.roomreserve.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUp {
    @NotNull @NotEmpty @NotBlank
    private String name;
    @NotNull @NotEmpty @NotBlank
    private String username;
    @NotNull @NotEmpty @NotBlank @Email
    private String email;
    @NotNull @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")
    private String password;
}

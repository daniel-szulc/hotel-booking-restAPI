package com.danielszulc.roomreserve.dto;

import com.danielszulc.roomreserve.utils.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUp {
    @NotNull @NotEmpty @NotBlank
    private String firstName;
    @NotNull @NotEmpty @NotBlank
    private String lastName;
    @NotNull @NotEmpty @NotBlank
    private String username;
    @NotNull @NotEmpty @NotBlank @Email
    private String email;
    @NotNull @ValidPassword
    private String password;
    @Pattern(regexp = "^ROLE_[A-Z_]+$")
    private String role;
}

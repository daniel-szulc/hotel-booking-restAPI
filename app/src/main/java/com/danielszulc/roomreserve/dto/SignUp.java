package com.danielszulc.roomreserve.dto;

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
    @NotNull @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
    private String password;
    @Pattern(regexp = "^ROLE_[A-Z_]+$")
    private String role;
}

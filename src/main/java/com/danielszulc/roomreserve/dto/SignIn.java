package com.danielszulc.roomreserve.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignIn {
    @NotNull @NotEmpty @NotBlank
    private String username;
    @NotNull @NotEmpty @NotBlank
    private String password;

}

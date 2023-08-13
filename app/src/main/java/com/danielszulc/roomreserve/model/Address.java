package com.danielszulc.roomreserve.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @NotNull
    @NotEmpty
    @NotBlank
    private String streetAddress;
    @NotNull
    @NotEmpty
    @NotBlank
    private String city;

    @NotNull
    @NotEmpty
    @NotBlank
    private String postalCode;

    @NotNull
    @NotEmpty
    @NotBlank
    private String state;

    @NotNull
    @NotEmpty
    @NotBlank
    private String country;

}

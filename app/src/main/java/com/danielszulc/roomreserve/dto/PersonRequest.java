package com.danielszulc.roomreserve.dto;

import com.danielszulc.roomreserve.model.Address;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequest {
    private Long id;
    @Email
    private String email;
    private String firstName;
    private String lastName;
    @Size(min = 9)
    private String phone;
    private Address address;
    @Enumerated(EnumType.STRING)
    private String gender;
}

package com.danielszulc.roomreserve.dto;

import com.danielszulc.roomreserve.model.Address;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Address address;
}

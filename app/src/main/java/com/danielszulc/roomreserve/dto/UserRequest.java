package com.danielszulc.roomreserve.dto;

import com.danielszulc.roomreserve.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        private Address address;
        private String gender;
    }

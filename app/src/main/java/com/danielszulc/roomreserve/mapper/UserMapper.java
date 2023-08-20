package com.danielszulc.roomreserve.mapper;

import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper<T> {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public PersonDTO convertToDTO(T entity) {
        PersonDTO personDTO = new PersonDTO();
        BeanUtils.copyProperties(entity, personDTO);
        return personDTO;
    }

    public User convertToEntity(SignUp signUpDto){
        return convertToEntity(signUpDto, Role.ROLE_CLIENT);
    }

    public User convertToEntity(SignUp signUpDto, Role role){
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole(role);
        return user;
    }

}

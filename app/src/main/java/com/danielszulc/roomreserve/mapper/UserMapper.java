package com.danielszulc.roomreserve.mapper;

import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.dto.UserDTO;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private  PasswordEncoder passwordEncoder;

    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public User convertToEntity(SignUp signUpDto){
        return convertToEntity(signUpDto, Role.ROLE_CLIENT);
    }

    public User convertToEntity(SignUp signUpDto, Role role){
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole(role);
        return user;
    }
}


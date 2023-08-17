package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.enums.Gender;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.mapper.UserMapper;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.AuthenticationService;
import com.danielszulc.roomreserve.service.UserService;
import com.danielszulc.roomreserve.service.UserValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtUtil;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    @Override
    public UserDTO registerUser(SignUp signUpDto) {
        userValidator.validateUsernameAndEmailAvailability(
                signUpDto.getUsername(), signUpDto.getEmail()
        );


        User user = userMapper.convertToEntity(signUpDto, Role.ROLE_CLIENT);
        User savedUser = userRepository.save(user);
        return userMapper.convertToDTO(savedUser);
    }

    @Override
    public AuthenticationResponse authenticateUser(SignIn loginDto) {
        try {
            Authentication authentication = authenticationService.authenticateWithCredentials(
                    loginDto.getUsername(), loginDto.getPassword()
            );
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);

            return new AuthenticationResponse(user.getEmail(), accessToken);
        } catch (BadCredentialsException e) {
            throw new InvalidLoginException("Invalid username or password!");
        }
    }

    @Override
    public String deleteUserByUsername(String username) {
        User userToDelete = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        User currentUser = getCurrentLoggedInUser();

        // Check if the logged user is an administrator and is trying to delete himself
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) || currentUser.getUsername().equals(username)) {
            throw new UnauthorizedException("You do not have permission to delete this user.");
        }

        userRepository.delete(userToDelete);

        log.info("Delete successful");
        return "User deleted successfully";
    }


    @Override
    public String updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User currentUser = getCurrentLoggedInUser();
        userValidator.validatePassword(currentUser.getPassword(), updatePasswordRequest.getCurrentPassword());

        String newPassword = updatePasswordRequest.getNewPassword();
        userRepository.updateUserPassword(currentUser.getUsername(), passwordEncoder.encode(newPassword));

        log.info("Update successful");
        return "Password updated successfully!";
    }

    @Override
    public String updatePersonalData(UserRequest userRequest) {
        User currentUser = getCurrentLoggedInUser();
        userValidator.validatePassword(currentUser.getPassword(), userRequest.getPassword());

        boolean updateOccurred = false;

        if (userRequest.getFirstName() != null && !userRequest.getFirstName().isEmpty()) {
            userRepository.updateFirstName(currentUser.getUsername(), userRequest.getFirstName());
            updateOccurred = true;
        }

        if (userRequest.getLastName() != null && !userRequest.getLastName().isEmpty()) {
            userRepository.updateLastName(currentUser.getUsername(), userRequest.getLastName());
            updateOccurred = true;
        }

        if (userRequest.getPhone() != null && !userRequest.getPhone().isEmpty()) {
            userRepository.updateUserPhone(currentUser.getUsername(), userRequest.getPhone());
            updateOccurred = true;
        }

        if (userRequest.getAddress() != null) {
            userRepository.updateUserAddress(currentUser.getUsername(), userRequest.getAddress());
            updateOccurred = true;
        }

        if (userRequest.getGender() != null) {
            Gender newGender = Gender.valueOf(userRequest.getGender().toUpperCase());
            userRepository.updateUserGender(currentUser.getUsername(), newGender);
            updateOccurred = true;
        }

        if (updateOccurred) {
            log.info("Update successful");
            return "Personal data updated successfully!";
        } else {
            throw new IllegalArgumentException("No valid field specified for update.");
        }
    }

    @Override
    public UserDTO createUserByAdmin(SignUp signUpDto) {
        Role role = determineUserRole(signUpDto.getRole());
        userValidator.validateAdminPermissions(getCurrentLoggedInUser());

        User user = userMapper.convertToEntity(signUpDto, role);
        User savedUser = userRepository.save(user);
        return userMapper.convertToDTO(savedUser);
    }

    @Override
    public UserDTO getUserData(){
        User user = getCurrentLoggedInUser();
        return userMapper.convertToDTO(user);
    }

    private User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    private Role determineUserRole(String requestedRole) {
        return (requestedRole != null) ? Role.valueOf(requestedRole) : Role.ROLE_CLIENT;
    }

    @Override
    public List<UserDTO> searchUsers(UserSearchRequest searchRequest) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            User currentUser = getCurrentLoggedInUser();
            if (Role.ROLE_ADMIN.equals(currentUser.getRole())) {
                // Admin can search for everyone, so we don't add any additional condition
            }
            else if (Role.ROLE_HOTEL.equals(currentUser.getRole())) {
                // The hotel can only search for customers
                predicates.add(cb.equal(root.get("role"), "ROLE_CLIENT"));
            } else {
                throw new UnauthorizedException("Clients are not allowed to search for users");
            }

            if (searchRequest.getUsername() != null && !searchRequest.getUsername().isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + searchRequest.getUsername() + "%"));
            }

            if (searchRequest.getEmail() != null && !searchRequest.getEmail().isEmpty()) {
                predicates.add(cb.like(root.get("email"), "%" + searchRequest.getEmail() + "%"));
            }

            if (searchRequest.getFirstName() != null && !searchRequest.getFirstName().isEmpty()) {
                predicates.add(cb.like(root.get("firstName"), "%" + searchRequest.getFirstName() + "%"));
            }

            if (searchRequest.getLastName() != null && !searchRequest.getLastName().isEmpty()) {
                predicates.add(cb.like(root.get("lastName"), "%" + searchRequest.getLastName() + "%"));
            }

            if (searchRequest.getPhone() != null && !searchRequest.getPhone().isEmpty()) {
                predicates.add(cb.like(root.get("phone"), "%" + searchRequest.getPhone() + "%"));
            }

            if (searchRequest.getAddress() != null) {

                if (searchRequest.getAddress().getCity() != null && !searchRequest.getAddress().getCity().isEmpty()) {
                    predicates.add(cb.like(root.get("address").get("city"), "%" + searchRequest.getAddress().getCity() + "%"));
                }

                if (searchRequest.getAddress().getStreetAddress() != null && !searchRequest.getAddress().getStreetAddress().isEmpty()) {
                    predicates.add(cb.like(root.get("address").get("streetAddress"), "%" + searchRequest.getAddress().getStreetAddress() + "%"));
                }

                if (searchRequest.getAddress().getPostalCode() != null && !searchRequest.getAddress().getPostalCode().isEmpty()) {
                    predicates.add(cb.like(root.get("address").get("postalCode"), "%" + searchRequest.getAddress().getPostalCode() + "%"));
                }

                if (searchRequest.getAddress().getState() != null && !searchRequest.getAddress().getState().isEmpty()) {
                    predicates.add(cb.like(root.get("address").get("state"), "%" + searchRequest.getAddress().getState() + "%"));
                }

                if (searchRequest.getAddress().getCountry() != null && !searchRequest.getAddress().getCountry().isEmpty()) {
                    predicates.add(cb.like(root.get("address").get("country"), "%" + searchRequest.getAddress().getCountry() + "%"));
                }
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<User> users = userRepository.findAll(spec);
        return users.stream().map(this.userMapper::convertToDTO).toList();
    }

    public UserDTO findUserByIdOrEmailOrUsername(Long id, String email, String username) {
        User user = null;

        if (id != null) {
            user = userRepository.findById(id).orElse(null);
        }

        if (user == null && email != null) {
            user = userRepository.findByEmail(email).orElse(null);
        }

        if (user == null && username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        User loggedInUser = getCurrentLoggedInUser();

        if (loggedInUser.getRole() == Role.ROLE_CLIENT ||
                (loggedInUser.getRole() == Role.ROLE_HOTEL && (user.getRole() == Role.ROLE_HOTEL || user.getRole() == Role.ROLE_ADMIN))) {
            throw new AccessDeniedException("Access is denied");
        }

        return userMapper.convertToDTO(user);
    }

}

package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.PersonRepository;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.AuthenticationService;
import com.danielszulc.roomreserve.service.UserService;
import com.danielszulc.roomreserve.service.UserValidator;
import com.danielszulc.roomreserve.utils.SpecificationUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl extends PersonServiceImpl<User> implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtUtil;
    private final UserValidator userValidator;
    private final AuthenticationService authenticationService;

    @Override
    public PersonRepository<User> getRepository() {
        return userRepository;
    }


    @Override
    public PersonDTO registerUser(SignUp signUpDto) {
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
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

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
        User user;

        if(Objects.equals(currentUser.getUsername(), userRequest.getUsername())
                || Objects.equals(currentUser.getId(), userRequest.getId())
                || Objects.equals(currentUser.getEmail(), userRequest.getEmail()))
        {
            userValidator.validatePassword(currentUser.getPassword(), userRequest.getPassword());
            user = currentUser;
        }
        else
        {
            userValidator.validateAdminPermissions(currentUser);
            user = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
        }

        userRequest.setId(user.getId());

        return super.updatePersonalData(userRequest);
    }

    @Override
    public PersonDTO createUserByAdmin(SignUp signUpDto) {
        Role role = determineUserRole(signUpDto.getRole());
        userValidator.validateAdminPermissions(getCurrentLoggedInUser());
        userValidator.validateUsernameAndEmailAvailability(
                signUpDto.getUsername(), signUpDto.getEmail()
        );
        User user = userMapper.convertToEntity(signUpDto, role);
        User savedUser = userRepository.save(user);
        return userMapper.convertToDTO(savedUser);
    }

    @Override
    public PersonDTO getUserData(){
        User user = getCurrentLoggedInUser();
        return userMapper.convertToDTO(user);
    }

    private User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    private Role determineUserRole(String requestedRole) {
        return (requestedRole != null) ? Role.valueOf(requestedRole) : Role.ROLE_CLIENT;
    }

    @Override
    public List<PersonDTO> searchUsers(UserSearchRequest searchRequest) {
        User currentUser = getCurrentLoggedInUser();
        Specification<User> roleBasedSpec = SpecificationUtils.createRoleBasedSpecification(currentUser.getRole());

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = SpecificationUtils.createPersonPredicates(root, cb, searchRequest.getEmail(), searchRequest.getFirstName(), searchRequest.getLastName(), searchRequest.getPhone(), searchRequest.getAddress());

            predicates.add(roleBasedSpec.toPredicate(root, query, cb));

            if (searchRequest.getUsername() != null && !searchRequest.getUsername().isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + searchRequest.getUsername() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<User> users = userRepository.findAll(spec);
        return users.stream().map(this.userMapper::convertToDTO).toList();
    }

    public PersonDTO findUserByIdOrEmailOrUsername(Long id, String email, String username) {
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

    @Override
    public List<PersonDTO> getAll() {
        User currentUser = getCurrentLoggedInUser();
        Specification<User> roleBasedSpec = SpecificationUtils.createRoleBasedSpecification(currentUser.getRole());

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(roleBasedSpec.toPredicate(root, query, cb));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<User> users = userRepository.findAll(spec);
        return users.stream().map(this.userMapper::convertToDTO).toList();
    }

}

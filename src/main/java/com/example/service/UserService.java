package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.default-users.admin.username}")
    private String defaultUserAdminUsername;

    @Value("${security.default-users.admin.password}")
    private String defaultUserAdminPassword;

    @PostConstruct
    private void init() {
        checkOrCreateDefaultUsers();
    }

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        log.info("Searching user by username '{}'", username);
        var userFoundOptional = userRepository.findByUsername(username);
        if (userFoundOptional.isEmpty()) {
            throw new UsernameNotFoundException("User with username '" + username + "' not found");
        }
        var userFound = userFoundOptional.get();
        log.info("User found by username '{}' - {}", username, userFound);
        return userFound;
    }

    protected void checkOrCreateDefaultUsers() {

        // Presence check
        var defaultAdminOptional = userRepository.findByUsername(defaultUserAdminUsername);
        if (defaultAdminOptional.isPresent()) {
            return;
        }

        log.warn("Default admin-user was not found, trying to create one");

        // Creating a user
        var defaultAdmin = new User();
        defaultAdmin.setUsername(defaultUserAdminUsername);
        defaultAdmin.setPassword(passwordEncoder.encode(defaultUserAdminPassword));
        defaultAdmin.setRole(Role.ADMIN);

        userRepository.save(defaultAdmin);

        log.info("Default admin-user was created");
    }
}

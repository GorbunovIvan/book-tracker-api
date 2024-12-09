package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoSpyBean
    private PasswordEncoder passwordEncoder;

    @Value("${security.default-users.admin.username}")
    private String defaultUserAdminUsername;
    @Value("${security.default-users.admin.password}")
    private String defaultUserAdminPassword;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnUserWhenLoadUserByUsername() {

        var user = easyRandom.nextObject(User.class);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        var userReceived = userService.loadUserByUsername(user.getUsername());
        assertNotNull(userReceived);
        assertEquals(user, userReceived);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenLoadUserByUsername() {

        var username = "test-username";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void shouldFindDefaultUserWhenCheckOrCreateDefaultUsers() {

        var defaultUserAdmin = new User();
        defaultUserAdmin.setUsername(defaultUserAdminUsername);

        when(userRepository.findByUsername(defaultUserAdminUsername)).thenReturn(Optional.of(defaultUserAdmin));

        userService.checkOrCreateDefaultUsers();

        verify(userRepository, times(1)).findByUsername(defaultUserAdminUsername);
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldNotFindDefaultUserAndCreateNewWhenCheckOrCreateDefaultUsers() {

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(defaultUserAdminPassword)).thenReturn("encoded-password");

        userService.checkOrCreateDefaultUsers();

        verify(userRepository, times(1)).findByUsername(defaultUserAdminUsername);
        verify(passwordEncoder, times(1)).encode(defaultUserAdminPassword);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertNotNull(captor);
        var userPersisted = captor.getValue();
        assertNotNull(userPersisted);
        assertEquals(defaultUserAdminUsername, userPersisted.getUsername());
        assertNotNull(userPersisted.getPassword());
        assertFalse(userPersisted.getPassword().isEmpty());
        assertEquals(Role.ADMIN, userPersisted.getRole());
    }
}
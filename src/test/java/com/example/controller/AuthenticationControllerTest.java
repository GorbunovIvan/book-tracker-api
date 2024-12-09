package com.example.controller;

import com.example.api.security.UserDetailsDto;
import com.example.model.Role;
import com.example.model.User;
import com.example.security.JwtTokenProvider;
import com.example.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoSpyBean
    private UserDetailsService userDetailsService;
    @MockitoSpyBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseURI = "/auth";

    @MockitoBean
    private UserService userService;

    private final User testUser = new User(1, "test-user", "test-password", Role.ADMIN);

    @BeforeEach
    void setUp() {
        when(userService.loadUserByUsername(testUser.getUsername())).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void shouldReturnResponseWithTrueWhenCheckToken() throws Exception {

        var token = jwtTokenProvider.createToken(testUser.getUsername(), testUser.getAuthorities());

        mockMvc.perform(post(baseURI + "/check-token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(jwtTokenProvider, times(1)).validateToken(token);
    }

    @Test
    void shouldReturnResponseWithFalseWhenCheckToken() throws Exception {

        var token = "wrong-token";

        mockMvc.perform(post(baseURI + "/check-token")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(token))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(jwtTokenProvider, times(1)).validateToken(token);
    }

    @Test
    void shouldReturnResponseWithMapWithTokenWhenLogin() throws Exception {

        var userDetailsDTO = new UserDetailsDto(testUser.getUsername(), testUser.getPassword());
        var userDetailsDTOJsonParam = objectMapper.writeValueAsString(userDetailsDTO);

        var jsonResponse = mockMvc.perform(post(baseURI + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userDetailsDTOJsonParam))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String> tokenDetailsReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(tokenDetailsReceived);
        assertEquals(2, tokenDetailsReceived.size());
        assertTrue(tokenDetailsReceived.containsKey("username"));
        assertTrue(tokenDetailsReceived.containsKey("token"));

        var usernameReceived = tokenDetailsReceived.get("username");
        assertNotNull(usernameReceived);
        assertEquals(testUser.getUsername(), usernameReceived);

        var tokenReceived = tokenDetailsReceived.get("token");
        assertNotNull(tokenReceived);
        assertFalse(tokenReceived.isEmpty());
        assertTrue(tokenReceived.length() > 100);

        verify(jwtTokenProvider, times(1)).createToken(anyString(), anyCollection());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, atLeastOnce()).loadUserByUsername(testUser.getUsername());
    }

    @Test
    void shouldReturnResponseWithUnauthorizedExceptionDueToWrongUsernameWhenLogin() throws Exception {

        var username = "wrong-username";

        var userDetailsDTO = new UserDetailsDto(username, testUser.getPassword());
        var userDetailsDTOJsonParam = objectMapper.writeValueAsString(userDetailsDTO);

        mockMvc.perform(post(baseURI + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userDetailsDTOJsonParam))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(anyString(), anyCollection());
    }

    @Test
    void shouldReturnResponseWithUnauthorizedExceptionDueToWrongPasswordWhenLogin() throws Exception {

        var password = "wrong-password";

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        var userDetailsDTO = new UserDetailsDto(testUser.getUsername(), password);
        var userDetailsDTOJsonParam = objectMapper.writeValueAsString(userDetailsDTO);

        mockMvc.perform(post(baseURI + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userDetailsDTOJsonParam))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(anyString(), anyCollection());
    }

    @Test
    void shouldReturnResponseWithMapWithTokenWhenLogout() throws Exception {
        mockMvc.perform(post(baseURI + "/logout"))
                .andExpect(status().isNoContent());
    }
}
package com.example.controller;

import com.example.api.security.UserDetailsDto;
import com.example.security.JwtTokenProvider;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    @PostMapping("/check-token")
    public ResponseEntity<Boolean> checkToken(@RequestBody String token) {
        if (token == null) {
            return ResponseEntity.ok(false);
        }
        try {
            boolean result = jwtTokenProvider.validateToken(token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.atInfo().setMessage(e.getMessage()).log();
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDetailsDto userDetailsDto) {

        try {
            log.info("Token required for username '{}'", userDetailsDto.username());

            var authenticationToken = new UsernamePasswordAuthenticationToken(userDetailsDto.username(), userDetailsDto.password());
            authenticationManager.authenticate(authenticationToken);

            var user = userService.loadUserByUsername(userDetailsDto.username());
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());

            var response = new HashMap<String, String>();
            response.put("username", user.getUsername());
            response.put("token", token);

            log.info("Token was provided to the user '{}'", user.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to provide token to the user - {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        var handler = new SecurityContextLogoutHandler();
        handler.logout(request, response, null);
    }
}

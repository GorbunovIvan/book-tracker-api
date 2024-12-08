package com.example.config;

import com.example.security.JwtConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConfigurer jwtConfigurer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity builder) throws Exception {
        return builder
                .csrf(AbstractHttpConfigurer::disable)  // Without disabling CSRF all http method types except "GET" will be forbidden (403)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(antMatcher("/auth/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET)).permitAll()
                        .anyRequest().authenticated()
                )
                .with(jwtConfigurer, customizer -> customizer.setBuilder(builder))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}

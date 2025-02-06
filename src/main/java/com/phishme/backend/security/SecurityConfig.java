package com.phishme.backend.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import com.phishme.backend.enums.UserRoles;
import com.phishme.backend.exceptions.ExceptionHandlerFilter;
import com.phishme.backend.security.jwt.JwtAuthenticationFilter;
import com.phishme.backend.security.jwt.JwtService;
import com.phishme.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@Order(1)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
        public static final String PERMITTED_URI[] = {
                        "/login",
                        "/register",
                        "/terms",
                        "/terms/registration"
        };
        public static final String[] PERMITTED_ROLES = Arrays.stream(UserRoles.values())
                        .map(role -> role.name().replace("ROLE_", ""))
                        .toArray(String[]::new);
        private static final String ADMIN_ONLY_URI[] = {

        };
        private final JwtService jwtService;
        private final UserService userService;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(CsrfConfigurer::disable)
                                .httpBasic(HttpBasicConfigurer::disable)
                                .formLogin(FormLoginConfigurer::disable)
                                .authorizeHttpRequests(request -> request
                                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                                .requestMatchers(PERMITTED_URI).permitAll()
                                                .requestMatchers(ADMIN_ONLY_URI)
                                                .hasRole(UserRoles.ADMIN.getKey().replace("ROLE_", ""))
                                                .anyRequest().hasAnyRole(PERMITTED_ROLES))
                                .sessionManagement(configurer -> configurer
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(new JwtAuthenticationFilter(
                                                PERMITTED_URI, jwtService,
                                                userService),
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class);

                return http.build();
        }
}

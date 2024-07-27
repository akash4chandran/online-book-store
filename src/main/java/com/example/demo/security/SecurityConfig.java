package com.example.demo.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.exception.ErrorResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/actuator/health", "/api/api-docs/**", "/api/swagger-ui/**", "/api/swagger/**")
                .permitAll().anyRequest().authenticated()).httpBasic(withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()));

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ErrorResponse errorResponse = buildErrorResponse(response, authException);
            buildServletOutputStream(response, errorResponse);
        };
    }

    private void buildServletOutputStream(HttpServletResponse response, ErrorResponse errorResponse)
            throws IOException {
        ServletOutputStream out = response.getOutputStream();
        new ObjectMapper().writeValue(out, errorResponse);
        out.flush();
    }

    private ErrorResponse buildErrorResponse(HttpServletResponse httpServletResponse, Exception ex) {
        List<String> details = new ArrayList<>();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(httpServletResponse.getStatus());
        errorResponse.setMessage(HttpStatus.valueOf(httpServletResponse.getStatus()).name());
        details.add(ex.getLocalizedMessage().split(":")[0]);
        errorResponse.setDetails(details);
        return errorResponse;
    }
}

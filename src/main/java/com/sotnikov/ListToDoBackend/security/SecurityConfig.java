package com.sotnikov.ListToDoBackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{

    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        "/auth/login",
                                        "/auth/registration",
                                        "/error"
                                )
                                .permitAll()
                                .requestMatchers(
                                        "/tasks/**",
                                        "/users/**"
                                ).authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(m ->
                        m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();


    }
}

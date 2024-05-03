package com.sotnikov.ListToDoBackend.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    private String jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (headerIsCorrect(authHeader)) {
            jwt = authHeader.substring(7);
            checkJWTAndSetAuthenticationIfCorrect();
        }

        filterChain.doFilter(request, response);
    }

    private boolean headerIsCorrect(String authHeader){
        return authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ");
    }

    private void checkJWTAndSetAuthenticationIfCorrect(){
        if (jwt.isBlank()) {
            throw new JWTVerificationException("JWT is incorrect: after bearer it is blank");
        }
        else {
            setAuthentication();
        }
    }

    private void setAuthentication(){
        String username = jwtUtil.validateTokenAndRetrieveClaim(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities());

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}

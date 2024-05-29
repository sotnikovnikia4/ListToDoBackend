package com.sotnikov.ListToDoBackend.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.exceptions.ExceptionMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    private String token;
    private HttpServletResponse response;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        //this.request = request;
        this.response = response;

        if (headerIsCorrect(authHeader)) {
            this.token = authHeader.substring(7);
            checkJWTAndSetAuthentication();
        }
        if(response.getStatus() != HttpServletResponse.SC_UNAUTHORIZED)
            filterChain.doFilter(request, response);
    }

    private boolean headerIsCorrect(String authHeader){
        return authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ");
    }

    private void checkJWTAndSetAuthentication() throws IOException {
        if (!token.isBlank()) {
            trySetAuthenticationOtherwiseSendErrorResponse();
        }
    }

    private void trySetAuthenticationOtherwiseSendErrorResponse() throws IOException {
        try {
            setAuthentication();
        } catch (JWTVerificationException | UsernameNotFoundException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessage("Jwt token is incorrect!", new Date())));
        }
    }

    private void setAuthentication(){
        String username = jwtUtil.validateTokenAndRetrieveClaim(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

}

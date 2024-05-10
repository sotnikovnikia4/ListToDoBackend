package com.sotnikov.ListToDoBackend.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sotnikov.ListToDoBackend.controllers.ExceptionController;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final ExceptionController exceptionController;

    private String token;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        this.request = request;
        this.response = response;

        if (headerIsCorrect(authHeader)) {
            this.token = authHeader.substring(7);
            checkJWTAndSetAuthentication();
        }
        filterChain.doFilter(request, response);
    }

    private boolean headerIsCorrect(String authHeader){
        return authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ");
    }

    private void checkJWTAndSetAuthentication(){
        if (!token.isBlank()) {
            trySetAuthentication();
        }
    }

    private void trySetAuthentication(){
        try {
            setAuthentication();
        } catch (JWTVerificationException | UsernameNotFoundException e) {
            handlerExceptionResolver.resolveException(request, response, exceptionController, e);
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

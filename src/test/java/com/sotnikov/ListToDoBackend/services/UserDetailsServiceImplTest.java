package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.UsersRepository;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsername() {
        User user = User.builder()
                .name("John")
                .login("123")
                .password("123")
                .build();

        when(usersRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

        UserDetails foundDetails = userDetailsService.loadUserByUsername(user.getLogin());

        Assertions.assertThat(foundDetails).isNotNull();
    }

    @Test
    void testLoadUserByUsername_ThrowsExceptionWhenNotFound() {
        User user = User.builder()
                .name("John")
                .login("123")
                .password("123")
                .build();

        when(usersRepository.findByLogin(user.getLogin())).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(user.getLogin()));
    }
}
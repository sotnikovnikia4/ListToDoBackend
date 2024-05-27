package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.UsersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void testRegister_ShouldEnrich() {
        when(passwordEncoder.encode(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0) + "123");
        when(usersRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        User user = User.builder().password("456").build();
        registrationService.register(user);

        Assertions.assertEquals("456123", user.getPassword());
        Assertions.assertNotNull(user.getRegisteredAt());
    }
}
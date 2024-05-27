package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.UsersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {


    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService userService;

    private static User authenticatedUser;

    @BeforeAll
    static void setUpBeforeAll(){
        authenticatedUser = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .build();
    }

    @Test
    public void testUpdateWhenUserExist_ShouldReturnUpdatedUser(){
        User userToSave = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .name("John")
                .login("123")
                .password("123")
                .build();

        when(usersRepository.findById(any())).thenReturn(Optional.ofNullable(User.builder().id(authenticatedUser.getId()).build()));
        when(passwordEncoder.encode(any())).thenAnswer(invocation -> invocation.getArgument(0) + "456");

        User updatedUser = userService.update(userToSave);

        assertThat(updatedUser.getName()).isEqualTo(userToSave.getName());
        assertThat(updatedUser.getLogin()).isEqualTo(userToSave.getLogin());
        assertThat(updatedUser.getPassword()).isEqualTo("123456");
    }

    @Test
    public void testUpdateWhenUserDoesNotExist_ShouldThrowException(){
        User userToSave = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .name("John")
                .login("123")
                .password("123")
                .build();

        when(usersRepository.findById(any())).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(UserDataNotChangedException.class, () -> userService.update(userToSave));
    }
}

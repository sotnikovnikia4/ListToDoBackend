package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureDataMongo
class UsersRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;
    private User user;

    @BeforeEach
    void setUp(){
        user = User.builder()
                .name("John")
                .login("123")
                .password("123")
                .build();
    }

    @Test
    void testSaveEntity(){

        User savedUser = usersRepository.save(user);
        user.setId(savedUser.getId());

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void testFindByLogin(){
        usersRepository.save(user);

        Optional<User> userByLogin = usersRepository.findByLogin("123");

        Assertions.assertThat(userByLogin.isPresent()).isEqualTo(true);
    }
}
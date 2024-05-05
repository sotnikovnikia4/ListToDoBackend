package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {
    private final UsersRepository usersRepository;

    public Optional<User> findOne(String login){
        return usersRepository.findByLogin(login);
    }

    public void update(User userToUpdate, User updatedUser){
        userToUpdate.setName(updatedUser.getName());
        userToUpdate.setPassword(updatedUser.getPassword());
        userToUpdate.setLogin(updatedUser.getLogin());
    }
}

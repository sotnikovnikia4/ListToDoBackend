package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.repotitories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UsersService {
    private final UsersRepository usersRepository;
    private final TasksRepository tasksRepository;

    private final PasswordEncoder passwordEncoder;

    public Optional<User> findOne(String login){
        return usersRepository.findByLogin(login);
    }

    @Transactional
    public User update(User updatedUser){
        Optional<User> userToUpdate = usersRepository.findById(updatedUser.getId());

        if(userToUpdate.isPresent()){
            userToUpdate.get().setName(updatedUser.getName());
            userToUpdate.get().setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            userToUpdate.get().setLogin(updatedUser.getLogin());
        }
        else{
            throw new UserDataNotChangedException("This user does not exist");
        }

        return userToUpdate.get();
    }

    @Transactional
    public void delete(User user){
        tasksRepository.deleteAllByUserId(user.getId());

        usersRepository.delete(user);
    }
}

package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    //@Transactional
    public void register(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);

    }
}

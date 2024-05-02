package com.sotnikov.ListToDoBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> foundUser = usersRepository.findByPhoneNumber(username);

        if(foundUser.isEmpty())
            throw new UsernameNotFoundException("Имя пользователя не найдено!");

        return new UserDetailsImpl(foundUser.get());
    }
}

package com.sotnikov.ListToDoBackend.security;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public final class UserDetailsHolder {
    public User getUserFromSecurityContext(){
        return ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public UserDetails getUserDetailsSecurityContext(){
        return (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

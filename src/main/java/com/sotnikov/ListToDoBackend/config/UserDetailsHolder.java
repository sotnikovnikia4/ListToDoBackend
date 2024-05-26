package com.sotnikov.ListToDoBackend.config;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class UserDetailsHolder {
    public static User getUserFromSecurityContext(){
        return ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public static UserDetails getUserDetailsSecurityContext(){
        return (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

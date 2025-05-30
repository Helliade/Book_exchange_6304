package com.example.demo.service;

import com.example.demo.Models.CustomUserDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.Models.Username;
import com.example.demo.repository.UsernameRepository;

import java.util.Objects;

@Component

public class CustomUserDetailsService implements UserDetailsService {


    private final UsernameRepository usernameRepository;

    public CustomUserDetailsService(UsernameRepository usernameRepository) {
        this.usernameRepository = usernameRepository;
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Username username = usernameRepository.findByLogin(login);
        if(Objects.isNull(username)) {
            System.out.println("User not available");
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(username);
    }
}

package com.cats.gft.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            if ("root".equals(username)) {
                logger.info("User 'root' found, returning user details");
                return new User("root", new BCryptPasswordEncoder().encode("root"), new ArrayList<>());
            } else {
                logger.warn("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        } catch (UsernameNotFoundException e) {
            logger.error("UsernameNotFoundException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
            throw new RuntimeException("An error occurred while loading user by username", e);
        }
    }
}
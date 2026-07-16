package com.example.tickets_api.service;

import com.example.tickets_api.model.AppUser;
import com.example.tickets_api.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges the application's {@link AppUser} store to Spring Security. Spring
 * calls this during authentication to load a user and its roles by username.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user and adapts it to Spring Security's {@link UserDetails}.
     *
     * @param username the login name to look up
     * @return the security view of the user (username, hashed password, role)
     * @throws UsernameNotFoundException if no user has that username
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
    }
}

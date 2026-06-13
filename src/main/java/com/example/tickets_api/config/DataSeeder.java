package com.example.tickets_api.config;

import com.example.tickets_api.model.AppUser;
import com.example.tickets_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser user = new AppUser(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "ADMIN"
                );
                userRepository.save(user);
            }
        };
    }
}

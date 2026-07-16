package com.example.tickets_api.config;

import com.example.tickets_api.model.AppUser;
import com.example.tickets_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds an initial admin account on application startup so the API is usable
 * (and demo-able) out of the box.
 */
@Configuration
public class DataSeeder {

    /**
     * Creates the default {@code admin} user the first time the app boots.
     * <p>
     * The check on {@code findByUsername} makes this idempotent: on later
     * restarts the user already exists and nothing is inserted, so it is safe to
     * run on every startup. The password is stored hashed via {@link PasswordEncoder},
     * never in plain text.
     * <p>
     * Note: {@code admin/admin123} is a well-known demo credential intended for
     * local/portfolio use; a real deployment should seed from configuration
     * rather than a hard-coded password.
     *
     * @param userRepository  store where the seeded user is persisted
     * @param passwordEncoder used to hash the password before saving
     * @return a runner executed once at startup
     */
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

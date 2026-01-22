package com.example.backend.config;

import com.example.backend.security.EncryptionUtil;
import com.example.backend.security.PasswordHashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test security configuration - disables security for testing
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public EncryptionUtil encryptionUtil() {
        return new EncryptionUtil(encryptionKey);
    }

    @Bean
    public PasswordHashUtil passwordHashUtil() {
        return new PasswordHashUtil();
    }
}

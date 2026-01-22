package com.example.backend.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DatabaseConnectionChecker implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConnectionChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            jdbcTemplate.execute("SELECT 1");
            logger.info("✅ PostgreSQL Database connection successful!");

            String dbVersion = jdbcTemplate.queryForObject(
                "SELECT version()",
                String.class
            );
            logger.info("📊 Database version: {}", dbVersion);

        } catch (Exception e) {
            logger.error("❌ Failed to connect to PostgreSQL database: {}", e.getMessage());
            logger.error("Please check your database configuration in application.properties");
        }
    }
}


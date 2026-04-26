package com.example.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    void cleanUpBusinessData() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    order_items,
                    orders,
                    products,
                    refresh_tokens
                RESTART IDENTITY CASCADE
                """);
    }
}

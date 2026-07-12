package com.cight;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class FlywayIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void flywayCreatesCoreTables() {
        Integer count = jdbcTemplate.queryForObject(
                """
                select count(*) from information_schema.tables
                where table_schema = 'public'
                  and table_name in ('build_events', 'failure_analyses', 'outbox_events')
                """,
                Integer.class
        );

        assertThat(count).isEqualTo(3);
    }
}

package com.crediya.authentication.infrastructure.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.Duration;

/**
 * R2DBC configuration for reactive database access
 * Configures PostgreSQL connection with connection pooling
 * Provides DatabaseClient bean for reactive database operations
 */
@Configuration
public class R2DBCConfig {
    
    @Value("${spring.r2dbc.url}")
    private String databaseUrl;
    
    @Value("${spring.r2dbc.username}")
    private String username;
    
    @Value("${spring.r2dbc.password}")
    private String password;
    
    @Value("${spring.r2dbc.pool.initial-size:5}")
    private int initialSize;
    
    @Value("${spring.r2dbc.pool.max-size:20}")
    private int maxSize;
    
    @Value("${spring.r2dbc.pool.max-idle-time:30m}")
    private String maxIdleTime;
    
    /**
     * Creates PostgreSQL connection factory
     * @return PostgresqlConnectionFactory
     */
    @Bean
    public PostgresqlConnectionFactory connectionFactory() {
        // Parse database URL to extract host, port, and database name
        String url = databaseUrl.replace("r2dbc:postgresql://", "");
        String[] parts = url.split("/");
        String[] hostPort = parts[0].split(":");
        String host = hostPort[0];
        int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 5432;
        String database = parts[1];
        
        PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .build();
        
        return new PostgresqlConnectionFactory(config);
    }
    
    /**
     * Creates connection pool for better performance
     * @param connectionFactory the PostgreSQL connection factory
     * @return ConnectionPool
     */
    @Bean
    public ConnectionPool connectionPool(PostgresqlConnectionFactory connectionFactory) {
        ConnectionPoolConfiguration poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(initialSize)
                .maxSize(maxSize)
                .maxIdleTime(Duration.parse("PT" + maxIdleTime))
                .build();
        
        return new ConnectionPool(poolConfig);
    }
    
    /**
     * Creates DatabaseClient for reactive database operations
     * @param connectionPool the connection pool
     * @return DatabaseClient
     */
    @Bean
    public DatabaseClient databaseClient(ConnectionPool connectionPool) {
        return DatabaseClient.create(connectionPool);
    }
}

package com.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {
    private final DatabaseManager databaseManager;

    public SchemaInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initialize() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS lab7_users (
                        id SERIAL PRIMARY KEY,
                        login TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS lab7_routes (
                        id BIGSERIAL PRIMARY KEY,
                        name TEXT NOT NULL,
                        coordinates_x DOUBLE PRECISION NOT NULL,
                        coordinates_y INTEGER NOT NULL CHECK (coordinates_y <= 71),
                        creation_date TIMESTAMPTZ NOT NULL,
                        from_x BIGINT,
                        from_y INTEGER,
                        from_z REAL,
                        to_x BIGINT NOT NULL,
                        to_y INTEGER NOT NULL,
                        to_z REAL NOT NULL,
                        distance REAL NOT NULL CHECK (distance > 1),
                        owner_login TEXT NOT NULL REFERENCES lab7_users(login) ON DELETE CASCADE
                    )
                    """);
        }
    }
}

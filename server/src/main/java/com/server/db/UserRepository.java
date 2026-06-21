package com.server.db;

import com.server.auth.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserRepository {
    private final DatabaseManager databaseManager;

    public UserRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean register(String login, String password) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO lab7_users(login, password_hash) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
            statement.setString(1, login);
            statement.setString(2, PasswordHasher.sha512(password));
            return statement.executeUpdate() == 1;
        }
    }

    public boolean authenticate(String login, String password) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT password_hash FROM lab7_users WHERE login = ?")) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getString("password_hash").equals(PasswordHasher.sha512(password));
            }
        }
    }
}

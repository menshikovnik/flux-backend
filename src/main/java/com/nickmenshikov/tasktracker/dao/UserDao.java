package com.nickmenshikov.tasktracker.dao;

import com.nickmenshikov.tasktracker.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserDao {

    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByUsername(String username) {

        String sql = "SELECT id, username, password_hash, created_at FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPasswordHash(resultSet.getString("password_hash"));
                user.setCreatedAt(resultSet.getTimestamp("created_at").toInstant());
                return Optional.of(user);
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, password_hash, created_at) values (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setTimestamp(3, Timestamp.from(user.getCreatedAt()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

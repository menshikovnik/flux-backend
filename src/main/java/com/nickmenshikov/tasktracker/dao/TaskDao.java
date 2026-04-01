package com.nickmenshikov.tasktracker.dao;

import com.nickmenshikov.tasktracker.model.Task;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TaskDao {

    private final DataSource dataSource;

    public TaskDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Task task) {
        String sql = "INSERT into tasks (title, description, created_at, status, priority, user_id) values (?, ?, ?, ?, ?, ?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setTimestamp(3, java.sql.Timestamp.from(task.getCreatedAt()));
            ps.setString(4, task.getStatus().name());
            ps.setString(5, task.getPriority().name());
            ps.setLong(6, task.getCreatorId().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

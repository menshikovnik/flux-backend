package com.nickmenshikov.tasktracker.dao;

import com.nickmenshikov.tasktracker.model.Priority;
import com.nickmenshikov.tasktracker.model.Status;
import com.nickmenshikov.tasktracker.model.Task;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            ps.setLong(6, task.getCreatorId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> findAll(String creatorId) {
        String sql = "SELECT * FROM tasks where user_id = ?";
        List<Task> tasks = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, Long.parseLong(creatorId));
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                tasks.add(setTask(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    public Optional<Task> getById(Long id, Long userId) {
        String sql = "SELECT * from tasks WHERE id = ? AND user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setLong(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(setTask(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private Task setTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        task.setStatus(Status.valueOf(rs.getString("status")));
        task.setPriority(Priority.valueOf(rs.getString("priority")));
        task.setCreatorId(rs.getLong("user_id"));

        return task;
    }
}

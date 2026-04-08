package com.nickmenshikov.tasktracker.service;

import com.nickmenshikov.tasktracker.dao.TaskDao;
import com.nickmenshikov.tasktracker.exception.TaskNotFoundException;
import com.nickmenshikov.tasktracker.model.Priority;
import com.nickmenshikov.tasktracker.model.Status;
import com.nickmenshikov.tasktracker.model.Task;

import java.time.Instant;
import java.util.List;

public class TaskService {
    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void createTask(String title, String description, String status, String priority, Long userId) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(Status.valueOf(status.toUpperCase()));
        task.setPriority(Priority.valueOf(priority.toUpperCase()));
        task.setCreatorId(userId);
        task.setCreatedAt(Instant.now());
        taskDao.save(task);
    }

    public List<Task> getAllTasks(String creatorId) {
        return taskDao.findAll(creatorId);
    }

    public Task getTaskById(Long id, Long userId) {
        return taskDao.getById(id, userId).orElseThrow(
                () -> new TaskNotFoundException("Task not found " + id)
        );
    }
}

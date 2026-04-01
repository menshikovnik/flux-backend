package com.nickmenshikov.tasktracker.service;

import com.nickmenshikov.tasktracker.dao.TaskDao;
import com.nickmenshikov.tasktracker.model.Priority;
import com.nickmenshikov.tasktracker.model.Status;
import com.nickmenshikov.tasktracker.model.Task;

public class TaskService {
    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void createTask(String title, String description, String status, String priority, String userId) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(Status.OPEN);
        task.setPriority(Priority.LOW);
        taskDao.save(task);
    }
}
